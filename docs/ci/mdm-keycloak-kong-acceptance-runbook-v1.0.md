# MDM Keycloak/Kong Acceptance Runbook v1.0

## Objective
Provide the bounded execution contract for Issue #19. This runbook is the operator/CI checklist; it does not claim the gate is green.

## Execution order

1. Start disposable Keycloak with the deterministic APMS test realm.
2. Wait for Keycloak readiness with an explicit timeout.
3. Resolve the trusted issuer and JWKS endpoint from the running fixture.
4. Start disposable Kong with the MDM route and JWT verification contract.
5. Wait for Kong readiness with an explicit timeout.
6. Start MDM with CI security configuration and a disposable PostgreSQL dependency.
7. Wait for MDM readiness with an explicit timeout.
8. Acquire deterministic test tokens from the disposable Keycloak realm.
9. Execute the authorization matrix:
   - missing token -> 401
   - malformed/invalid/expired token -> 401
   - wrong issuer/audience -> 401
   - valid token without authority -> 403
   - valid MDM authority -> 2xx
   - approved service identity -> 2xx where permitted
10. Verify identity integrity:
   - caller payload cannot override verified actor
   - audit actor is derived from verified security context
   - propagated identity remains consistent across request handling
11. Publish machine-readable test results and logs as CI artifacts.
12. Clean up all disposable containers, networks and temporary credentials.

## Failure rules

Any failed authentication, authorization, identity-integrity, readiness, timeout or cleanup assertion fails the acceptance job.

No retry may convert a deterministic functional failure into acceptance. Infrastructure retries are permitted only for bounded startup/readiness conditions and must remain visible in logs.

## Evidence

The acceptance gate may only be marked GREEN when the CI run proves the complete path through the actual disposable Keycloak/Kong stack and the MDM application security boundary.

## Security

No production tokens, production client secrets, permanent credentials or environment-specific secrets are committed to the repository.
