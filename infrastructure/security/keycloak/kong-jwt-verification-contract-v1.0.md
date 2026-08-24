# MDM Keycloak/Kong JWT Verification Contract v1.0

## Purpose

Define the deterministic authentication boundary between Keycloak-issued access tokens, Kong, and MDM before role-based authorization acceptance is enabled.

## Authentication contract

- Issuer: supplied by the active environment configuration and must equal the trusted Keycloak issuer.
- Signature: Kong must validate the JWT signature with the exact Keycloak signing key identified by the token `kid`.
- Algorithm: only the explicitly approved asymmetric algorithm is accepted.
- Expiry: expired tokens are rejected.
- Not-before: the bounded CI fixture does not require Kong-side `nbf` validation because the Keycloak-issued acceptance token is the authoritative validity source.

## Protected endpoint

The acceptance target is the non-mutating MDM security probe:

`GET /mdm/api/v1/security/probe`

## Required authentication evidence

- missing token -> 401
- invalid signature -> 401
- wrong issuer -> 401
- expired token -> 401
- valid Keycloak-issued token -> 2xx

## Separation of concerns

Role-based authorization, audience enforcement, and identity-integrity assertions are a subsequent acceptance slice. They are not combined with the base authentication checkpoint.

## Environment rule

Issuer and JWKS location are environment configuration. CI fixtures must use disposable Keycloak credentials and must not embed production credentials or tokens.

## Acceptance evidence

This contract is GREEN only when the bounded CI workflow demonstrates the complete Keycloak-issued JWT authentication path through Kong to the protected MDM endpoint.
