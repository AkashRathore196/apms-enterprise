# APMS Security Infrastructure

## MDM local security acceptance

The MDM security acceptance stack is disposable and intended for local/CI validation only.

Components:
- Keycloak: deterministic APMS realm and test identities.
- Kong: declarative API gateway contract.
- MDM service: supplied separately by the acceptance workflow/runtime harness.

### Current state

The repository now contains the disposable Keycloak realm fixture, Docker Compose topology, and Kong declarative boundary.

The JWT verification configuration is governed by the Keycloak issuer/JWKS contract. The acceptance workflow must prove issuer, audience, signature and expiry validation before the authorization gate can report GREEN. A route existing behind Kong is not sufficient evidence of authentication or authorization.

### Security rules

- No production secrets or bearer tokens are committed.
- Test credentials exist only in the disposable fixture and must never be reused outside local/CI acceptance.
- Exact APMS production roles/scopes remain governed by the security baseline.
- Authorization is valid only when both gateway authentication and MDM application authorization are evidenced.
- The acceptance workflow must have bounded readiness, test and cleanup timeouts.

## Gate status

Keycloak/Kong authorization remains OPEN until executable CI evidence proves authentication, authorization, JWT issuer/audience/signature/expiry validation, service identity, identity integrity, and audit actor propagation.
