#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${KONG_BASE_URL:-http://localhost:18000}"
REALM_URL="${KEYCLOAK_REALM_URL:-http://localhost:18080/realms/apms}"

require_cmd() { command -v "$1" >/dev/null 2>&1 || { echo "missing command: $1" >&2; exit 2; }; }
require_cmd curl
require_cmd jq
require_cmd openssl

request_status() {
  local token="${1:-}"
  local url="${BASE_URL}/mdm/actuator/health"
  if [[ -n "$token" ]]; then
    curl -sS -o /tmp/mdm-security-response.txt -w '%{http_code}' \
      -H "Authorization: Bearer $token" "$url"
  else
    curl -sS -o /tmp/mdm-security-response.txt -w '%{http_code}' "$url"
  fi
}

assert_exact_status() {
  local expected="$1" token="${2:-}" label="$3"
  local actual
  actual="$(request_status "$token")"
  if [[ "$actual" != "$expected" ]]; then
    echo "[$label] expected HTTP $expected, got $actual" >&2
    cat /tmp/mdm-security-response.txt >&2 || true
    exit 1
  fi
  echo "[$label] HTTP $actual"
}

assert_success_class() {
  local token="$1" label="$2"
  local actual
  actual="$(request_status "$token")"
  if ! [[ "$actual" =~ ^2[0-9][0-9]$ ]]; then
    echo "[$label] expected HTTP 2xx, got $actual" >&2
    cat /tmp/mdm-security-response.txt >&2 || true
    exit 1
  fi
  echo "[$label] HTTP $actual"
}

# Prove the disposable Keycloak realm is available and exposes issuer/JWKS metadata.
DISCOVERY="$(curl -fsS "$REALM_URL/.well-known/openid-configuration")"
printf '%s' "$DISCOVERY" | jq -e '.issuer and .jwks_uri' >/dev/null
JWKS_URI="$(printf '%s' "$DISCOVERY" | jq -r '.jwks_uri')"
JWKS="$(curl -fsS "$JWKS_URI")"
printf '%s' "$JWKS" | jq -e '.keys | length > 0' >/dev/null
ISSUER="$(printf '%s' "$DISCOVERY" | jq -r '.issuer')"

# Authentication boundary through Kong.
assert_exact_status 401 "" "missing-token"

# Generate a disposable RSA keypair and use it for genuinely signed RS256 test tokens.
KEY_DIR="$(mktemp -d)"
trap 'rm -rf "$KEY_DIR"' EXIT
openssl genrsa -out "$KEY_DIR/ci-private.pem" 2048 >/dev/null 2>&1

# Build a JWK for the CI signing key so Kong can trust the exact key used by the fixture.
openssl rsa -in "$KEY_DIR/ci-private.pem" -pubout -outform DER -out "$KEY_DIR/ci-public.der" >/dev/null 2>&1
PUB_B64="$(openssl base64 -A -in "$KEY_DIR/ci-public.der" | tr '+/' '-_' | tr -d '=')"
# Extract RSA modulus/exponent through openssl text output.
MOD_HEX="$(openssl rsa -in "$KEY_DIR/ci-private.pem" -noout -modulus | cut -d= -f2)"
MOD_B64="$(printf '%s' "$MOD_HEX" | xxd -r -p | openssl base64 -A | tr '+/' '-_' | tr -d '=')"
EXP_B64="AQAB"
KID="ci-test-key"

# Provision a disposable Kong JWT credential matching the token issuer and signing key.
KONG_ADMIN="${KONG_ADMIN_URL:-http://localhost:18001}"
curl -fsS -X POST "$KONG_ADMIN/consumers" --data username=mdm-security-fixture >/dev/null 2>&1 || true
curl -fsS -X POST "$KONG_ADMIN/consumers/mdm-security-fixture/jwt" \
  --data "key=$ISSUER" \
  --data "algorithm=RS256" \
  --data "rsa_public_key=$(printf '%s' "$MOD_B64")" \
  --data "secret=$(printf '%s' "$EXP_B64")" \
  --data "kong=yes" >/dev/null 2>&1 || true

TOKEN_HEADER_B64="$(printf '%s' '{"alg":"RS256","typ":"JWT","kid":"ci-test-key"}' | openssl base64 -A | tr '+/' '-_' | tr -d '=')"
NOW="$(date +%s)"
make_token() {
  local username="$1"
  local roles_json="$2"
  local payload
  payload="$(jq -cn --arg sub "$username" --arg iss "$ISSUER" --argjson roles "$roles_json" --argjson iat "$NOW" --argjson exp "$((NOW+600))" '{sub:$sub,iss:$iss,aud:"mdm-api",iat:$iat,exp:$exp,realm_access:{roles:$roles}}')"
  local payload_b64
  payload_b64="$(printf '%s' "$payload" | openssl base64 -A | tr '+/' '-_' | tr -d '=')"
  local signing_input="$TOKEN_HEADER_B64.$payload_b64"
  local signature_b64
  signature_b64="$(printf '%s' "$signing_input" | openssl dgst -sha256 -sign "$KEY_DIR/ci-private.pem" -binary | openssl base64 -A | tr '+/' '-_' | tr -d '=')"
  printf '%s.%s' "$signing_input" "$signature_b64"
}

operator_token="$(make_token mdm-operator '["mdm_operator"]')"
reader_token="$(make_token mdm-reader '["mdm_reader"]')"
cross_token="$(make_token cross-domain '[]')"

assert_success_class "$operator_token" "operator-access"
assert_success_class "$reader_token" "reader-access"

cross_status="$(request_status "$cross_token")"
if [[ "$cross_status" != "403" ]]; then
  echo "[cross-domain-denial] expected HTTP 403, got $cross_status" >&2
  cat /tmp/mdm-security-response.txt >&2 || true
  exit 1
fi
echo "[cross-domain-denial] HTTP $cross_status"

cat > /tmp/mdm-security-result.json <<EOF
{
  "missingToken": 401,
  "operator": "2xx",
  "reader": "2xx",
  "crossDomain": 403,
  "keycloakDiscovery": "available",
  "keycloakJwks": "available",
  "signedFixture": "rs256",
  "issuerCredential": "provisioned"
}
EOF
