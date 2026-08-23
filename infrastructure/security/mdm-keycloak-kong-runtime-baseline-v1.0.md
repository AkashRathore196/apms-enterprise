# MDM Keycloak/Kong Runtime Baseline v1.0

## Purpose

Define the minimum executable runtime boundary for the MDM authorization acceptance gate.

## Runtime responsibilities

### Keycloak
- token issuer and identity authority
- issuer/audience validation contract
- token expiry validation
- role/scope claims
- service identity claims

### Kong
- external API gateway
- authentication enforcement
- JWT/OIDC verification integration boundary
- upstream identity propagation
- rejection of unauthenticated requests

### MDM service
- application-level authorization
- verified security-context identity only
- business authorization by approved MDM role/scope
- actor identity propagation into audit
- caller-supplied actor identity must not override verified identity

## Environment rule

This baseline is runtime-oriented but environment-neutral. Secrets, client credentials, signing material and environment-specific endpoints must be supplied through managed configuration/secrets and must never be committed.

## Acceptance scenarios

1. No token → 401.
2. Invalid/expired token → 401.
3. Valid token without required MDM authority → 403.
4. Valid token with approved MDM authority → allowed.
5. Cross-domain identity without MDM authority → 403.
6. Approved service identity → allowed.
7. Caller-supplied actor field cannot replace verified principal.
8. Audit actor comes from verified security context.

## Implementation boundary

The first implementation slice may use a local deterministic identity provider/test realm for bounded CI. It must preserve the same issuer/audience/role/scope semantics required for Keycloak. Production Keycloak and Kong endpoints remain deployment configuration, not application source.

## Exit criterion

The MDM authorization gate becomes executable when the repository contains bounded Keycloak-compatible identity fixtures, Kong gateway configuration, MDM authorization enforcement, and CI evidence covering the acceptance matrix.
