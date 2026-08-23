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
  local response_file=/tmp/keycloak-token-response.json
  local http_code

  http_code="$(curl -sS -o "$response_file" -w '%{http_code}' -X POST "$TOKEN_URL" \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode "client_id=$CLIENT_ID" \
    --data-urlencode 'grant_type=password' \
    --data-urlencode "username=$user" \
    --data-urlencode "password=$password")"

  if [[ "$http_code" != "200" ]]; then
    echo "[token:$user] Keycloak token request failed with HTTP $http_code" >&2
    cat "$response_file" >&2 || true
    exit 1
  fi

  local token
  token="$(jq -r '.access_token // empty' "$response_file")"
  if [[ -z "$token" ]]; then
    echo "[token:$user] Keycloak response did not contain access_token" >&2
    cat "$response_file" >&2 || true
    exit 1
  fi
  printf '%s' "$token"
}

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

assert_exact_status 401 "" "missing-token"

operator_token="$(get_token mdm-operator operator)"
reader_token="$(get_token mdm-reader reader)"
cross_token="$(get_token cross-domain cross)"

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
  "crossDomain": 403
}
EOF
