#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${KONG_BASE_URL:-http://localhost:18000}"
REALM_URL="${KEYCLOAK_REALM_URL:-http://localhost:18080/realms/apms}"
TOKEN_URL="$REALM_URL/protocol/openid-connect/token"
CLIENT_ID="${KEYCLOAK_CLIENT_ID:-mdm-test-client}"
CLIENT_SECRET="${KEYCLOAK_CLIENT_SECRET:-ci-mdm-test-secret}"

require_cmd() { command -v "$1" >/dev/null 2>&1 || { echo "missing command: $1" >&2; exit 2; }; }
require_cmd curl
require_cmd jq

request_status() {
  local token="${1:-}"
  local url="${BASE_URL}/mdm/api/v1/security/probe"
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

get_token() {
  local response status body
  response="$(curl -sS -X POST "$TOKEN_URL" \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode "client_id=$CLIENT_ID" \
    --data-urlencode "client_secret=$CLIENT_SECRET" \
    --data-urlencode 'grant_type=client_credentials' \
    -w $'\n%{http_code}')"
  status="$(printf '%s' "$response" | tail -n1)"
  body="$(printf '%s' "$response" | sed '$d')"
  if [[ "$status" != "200" ]]; then
    echo "[token] Keycloak client-credentials request failed with HTTP $status" >&2
    printf '%s\n' "$body" >&2
    exit 1
  fi
  printf '%s' "$body" | jq -er '.access_token'
}

DISCOVERY="$(curl -fsS "$REALM_URL/.well-known/openid-configuration")"
printf '%s' "$DISCOVERY" | jq -e '.issuer and .jwks_uri' >/dev/null
curl -fsS "$(printf '%s' "$DISCOVERY" | jq -r '.jwks_uri')" | jq -e '.keys | length > 0' >/dev/null

assert_exact_status 401 "" "missing-token"

service_token="$(get_token)"

operator_status="$(request_status "$service_token")"
if [[ "$operator_status" != "200" && "$operator_status" != "204" ]]; then
  echo "[service-identity-access] expected approved service identity, got $operator_status" >&2
  cat /tmp/mdm-security-response.txt >&2 || true
  exit 1
fi

echo "[service-identity-access] HTTP $operator_status"

cat > /tmp/mdm-security-result.json <<EOF
{
  "missingToken": 401,
  "serviceIdentity": "2xx",
  "keycloakDiscovery": "available",
  "keycloakJwks": "available",
  "tokenAuthority": "keycloak-issued"
}
EOF
