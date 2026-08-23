#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${KONG_BASE_URL:-http://localhost:18000}"
REALM_URL="${KEYCLOAK_REALM_URL:-http://localhost:18080/realms/apms}"
TOKEN_URL="$REALM_URL/protocol/openid-connect/token"
CLIENT_ID="${KEYCLOAK_CLIENT_ID:-mdm-test-client}"

require_cmd() { command -v "$1" >/dev/null 2>&1 || { echo "missing command: $1" >&2; exit 2; }; }
require_cmd curl
require_cmd jq

get_token() {
  local user="$1" password="$2"
  curl -fsS -X POST "$TOKEN_URL" \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode "client_id=$CLIENT_ID" \
    --data-urlencode 'grant_type=password' \
    --data-urlencode "username=$user" \
    --data-urlencode "password=$password" | jq -r '.access_token'
}

assert_status() {
  local expected="$1" token="${2:-}" label="$3"
  local url="${BASE_URL}/mdm/actuator/health"
  local actual
  if [[ -n "$token" ]]; then
    actual="$(curl -sS -o /tmp/mdm-security-response.txt -w '%{http_code}' -H "Authorization: Bearer $token" "$url")"
  else
    actual="$(curl -sS -o /tmp/mdm-security-response.txt -w '%{http_code}' "$url")"
  fi
  if [[ "$actual" != "$expected" ]]; then
    echo "[$label] expected HTTP $expected, got $actual" >&2
    cat /tmp/mdm-security-response.txt >&2 || true
    exit 1
  fi
  echo "[$label] HTTP $actual"
}

# Authentication boundary checks.
assert_status 401 "" "missing-token"

operator_token="$(get_token mdm-operator operator)"
reader_token="$(get_token mdm-reader reader)"
cross_token="$(get_token cross-domain cross)"

# Authorization is expected to be enforced by the MDM application behind Kong.
assert_status 2xx_PLACEHOLDER "$operator_token" "operator-access"
