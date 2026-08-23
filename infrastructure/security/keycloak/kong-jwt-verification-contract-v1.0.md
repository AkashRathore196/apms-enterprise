# MDM Keycloak/Kong JWT Verification Contract v1.0

## Purpose

Define the minimum deterministic contract between Keycloak-issued access tokens, Kong, and MDM before authorization acceptance can be marked GREEN.

## Verification authority

- Issuer: supplied by the active environment configuration; must equal the trusted Keycloak issuer.
- Audience: must equal the MDM API audience configured for the environment.
- Signature/JWKS: Kong must validate JWT signatures using the trusted Keycloak JWKS endpoint; no shared static signing secret is committed to Git.
- Expiry/not-before: tokens outside their validity window are rejected.
- Algorithm: only explicitly approved asymmetric signing algorithms are accepted.

## Claim propagation

Kong must preserve the verified identity context required by MDM. MDM must derive authorization and audit actor identity from the verified security context, never from caller-supplied payload fields.

## Required rejection behavior

- missing token -> 401
- malformed token -> 401
- invalid signature -> 401
- wrong issuer -> 401
- wrong audience -> 401
- expired/not-yet-valid token -> 401
- valid token without required authority -> 403

## Environment rule

The issuer URL, audience, JWKS location, and approved roles/scopes are environment configuration. CI fixtures must provide these values without embedding production credentials or tokens.

## Acceptance evidence

The contract is considered executable only when a bounded CI workflow demonstrates all required authentication and authorization cases through the actual disposable Keycloak/Kong path.
