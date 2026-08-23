# MDM Keycloak/Kong Acceptance Plan v1.0

## Objective

Provide a bounded, repeatable path to turn the MDM authorization matrix into executable CI evidence.

## Runtime contract

```text
Client
  -> Kong
     -> token verification / authentication boundary
     -> MDM service
        -> Spring Security authority evaluation
        -> verified principal / claims
        -> business operation
        -> audit actor from verified security context
```

## Local acceptance profile

The local acceptance profile must provide:

- disposable Keycloak instance
- disposable Kong instance
- disposable MDM service instance
- deterministic realm/client/role/scope fixtures
- no production secrets
- bounded startup/readiness timeouts
- isolated network and ports
- cleanup after every run

## Required test identities

Use deterministic fixture identities representing:

1. MDM operator with approved MDM authority.
2. Read-only MDM consumer with approved read authority.
3. Cross-domain consumer without MDM authority.
4. Service identity with approved service-to-service scope.
5. Invalid/expired/malformed token fixtures.

Exact business role/scope names must come from the APMS security baseline before production freeze.

## Acceptance scenarios

### Authentication

- request without token -> 401
- malformed token -> 401
- expired token -> 401
- invalid issuer/audience -> 401

### Authorization

- valid token without required authority -> 403
- approved MDM authority -> 2xx
- cross-domain caller without MDM authority -> 403
- approved service identity -> 2xx where operation permits

### Identity integrity

- caller-supplied actor identity cannot override verified principal
- audit actor is derived from verified security context
- token subject/claims are propagated consistently across the request

## CI boundary

Create one bounded workflow dedicated to authorization acceptance. It must:

1. start disposable identity/gateway dependencies;
2. wait for readiness with explicit timeouts;
3. start the MDM service with test configuration;
4. obtain deterministic test tokens;
5. execute the matrix;
6. publish machine-readable test results;
7. clean up all containers/network resources;
8. fail on authorization or identity-integrity regression.

## Exit criterion

Issue #18 can move to GREEN only when the acceptance workflow passes repeatedly in CI and the evidence demonstrates both gateway and application enforcement plus verified actor/audit propagation.

## Safety rule

Do not embed secrets, bearer tokens, client secrets, or environment-specific URLs in Git. CI fixtures must be generated or supplied through non-production test configuration.
