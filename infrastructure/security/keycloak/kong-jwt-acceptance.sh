#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${KONG_BASE_URL:-http://localhost:18000}"
ADMIN_URL="${KONG_ADMIN_URL:-http://localhost:18001}"
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

DISCOVERY="$(curl -fsS "$REALM_URL/.well-known/openid-configuration")"
printf '%s' "$DISCOVERY" | jq -e '.issuer and .jwks_uri' >/dev/null
ISSUER="$(printf '%s' "$DISCOVERY" | jq -r '.issuer')"
JWKS_URI="$(printf '%s' "$DISCOVERY" | jq -r '.jwks_uri')"
curl -fsS "$JWKS_URI" | jq -e '.keys | length > 0' >/dev/null

assert_exact_status 401 "" "missing-token"

KEY_DIR="$(mktemp -d)"
trap 'rm -rf "$KEY_DIR"' EXIT
openssl genrsa -out "$KEY_DIR/ci-private.pem" 2048 >/dev/null 2>&1

# Kong's declarative JWT plugin requires the matching RSA public key. Register the
# issuer as the credential key and fail loudly if provisioning is rejected.
PUBLIC_KEY="$(openssl rsa -in "$KEY_DIR/ci-private.pem" -pubout 2>/dev/null)"
KONG_CREATE_RESPONSE="$(curl -sS -w $'\n%{http_code}' -X POST "$ADMIN_URL/consumers" --data username=mdm-security-fixture)"
KONG_CREATE_STATUS="$(printf '%s' "$KONG_CREATE_RESPONSE" | tail -n1)"
if [[ "$KONG_CREATE_STATUS" != "201" && "$KONG_CREATE_STATUS" != "409" ]]; then
  echo "$KONG_CREATE_RESPONSE" >&2
  echo "failed to create disposable Kong consumer" >&2
  exit 1
fi

KONG_JWT_RESPONSE="$(curl -sS -w $'\n%{http_code}' -X POST "$ADMIN_URL/consumers/mdm-security-fixture/jwt" \
  --data-urlencode "key=$ISSUER" \
  --data-urlencode "algorithm=RS256" \
  --data-urlencode "rsa_public_key=$PUBLIC_KEY")"
KONG_JWT_STATUS="$(printf '%s' "$KONG_JWT_RESPONSE" | tail -n1)"
if [[ "$KONG_JWT_STATUS" != "201" && "$KONG_JWT_STATUS" != "409" ]]; then
  echo "$KONG_JWT_RESPONSE" >&2
  echo "failed to provision disposable Kong JWT credential" >&2
  exit 1
fi

HEADER_B64="$(printf '%s' '{"alg":"RS256","typ":"JWT"}' | openssl base64 -A | tr '+/' '-_' | tr -d '=')"
NOW="$(date +%s)"
make_token() {
  local username="$1" roles_json="$2"
  local payload payload_b64 signing_input signature_b64
  payload="$(jq -cn --arg sub "$username" --arg iss "$ISSUER" --argjson roles "$roles_json" --argjson iat "$NOW" --argjson exp "$((NOW+600))" '{sub:$sub,iss:$iss,aud:"mdm-api",iat:$iat,exp:$exp,realm_access:{roles:$roles}}')"
  payload_b64="$(printf '%s' "$payload" | openssl base64 -A | tr '+/' '-_' | tr -d '=')"
  signing_input="$HEADER_B64.$payload_b64"
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
