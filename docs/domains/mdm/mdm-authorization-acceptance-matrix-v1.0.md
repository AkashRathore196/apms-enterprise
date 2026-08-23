# APMS MDM Authorization Acceptance Matrix v1.0

## Purpose

Define the executable authorization boundary for MDM Party/Organization before production acceptance. This artifact is a test contract; it does not claim that the runtime gateway/identity stack is already implemented.

## Boundary

Kong is the API gateway enforcement point and Keycloak is the identity/token authority. MDM services must authorize requests using verified identity and claims rather than trusting caller-supplied identity fields.

## Acceptance matrix

| Scenario | Expected result | Evidence |
|---|---|---|
| No access token | 401 Unauthorized | Gateway/API test |
| Malformed/invalid token | 401 Unauthorized | Gateway/API test |
| Expired token | 401 Unauthorized | Gateway/API test |
| Valid token, missing required role/scope | 403 Forbidden | Authorization test |
| Valid token, allowed MDM role/scope | 2xx | Authorization test |
| Cross-domain caller without MDM authority | 403 Forbidden | Authorization test |
| Service-to-service identity with approved scope | 2xx | Integration test |
| Caller attempts identity substitution in payload | Request rejected or governed identity ignored | API/business-rule test |
| Audit records actor identity from verified security context | Accepted | Audit integration test |

## Required claims / authorities

The exact role/scope taxonomy must be approved with the APMS security baseline before production freeze. The implementation must not rely on hard-coded business assumptions that conflict with Keycloak/Kong governance.

## Test layers

1. Gateway-level authentication behavior.
2. Application-level authorization behavior.
3. Integration proof of propagated authenticated identity.
4. Audit proof that actor identity is derived from the verified security context.

## Gate status

OPEN — executable implementation and CI evidence are not yet present in the repository.

## Exit criterion

Gate becomes GREEN only when the matrix is implemented as bounded tests, executed in CI, and the resulting evidence is linked to the MDM acceptance register.
