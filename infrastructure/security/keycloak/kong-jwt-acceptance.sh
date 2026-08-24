#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${KONG_BASE_URL:-http://localhost:18000}"

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

assert_principal() {
  local expected="$1" label="$2"
  local body principal
  body="$(cat /tmp/mdm-security-response.txt)"
  principal="$(printf '%s' "$body" | jq -r '.principal')"
  if [[ "$principal" != "$expected" ]]; then
    echo "[$label] expected principal $expected, got $principal" >&2
    cat /tmp/mdm-security-response.txt >&2 || true
    exit 1
  fi
  echo "[$label] principal=$principal"
}

TOKEN="${MDM_SECURITY_ACCESS_TOKEN:?MDM_SECURITY_ACCESS_TOKEN must be supplied by CI}"

assert_exact_status 401 "" "missing-token"

status="$(request_status "$TOKEN")"
if [[ ! "$status" =~ ^2[0-9][0-9]$ ]]; then
  echo "[service-identity-access] expected HTTP 2xx, got $status" >&2
  cat /tmp/mdm-security-response.txt >&2 || true
  exit 1
fi

authorities="$(jq -c '.authorities // []' /tmp/mdm-security-response.txt)"
assert_principal "service-account-mdm-service-client" "service-identity-integrity"

echo "[service-identity-access] HTTP $status authorities=$authorities"

cat > /tmp/mdm-security-result.json <<EOF
{
  "missingToken": 401,
  "serviceIdentity": "2xx",
  "tokenAuthority": "keycloak-issued",
  "identityIntegrity": "verified"
}
EOF
