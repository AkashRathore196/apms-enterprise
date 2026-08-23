# MDM Security Acceptance Holistic Baseline v1.0

## Purpose

Freeze the execution boundary for MDM security acceptance after repeated CI failures exposed three mixed concerns.

## Required separation

1. **Security runtime**
   - Keycloak issuer/discovery/JWKS availability.
   - Kong DB-less declarative JWT credential.
   - JWT signature, issuer, audience, expiry, and not-before validation.
   - MDM application-side JWT resource-server validation.
   - Role/scope authorization.

2. **Observability runtime**
   - OpenTelemetry export is not part of the security acceptance precondition.
   - Security acceptance must use a bounded profile that disables external OTLP export or explicitly supplies a collector.
   - A missing collector must not cause the MDM process to terminate during a security test.

3. **Business/API target**
   - `/actuator/health` is readiness-only and must not be used as the positive authorization target.
   - Positive/negative authorization must exercise a governed MDM API endpoint once the API contract is identified.
   - Readiness evidence and authorization evidence are separate artifacts.

## Acceptance sequence

```text
PostgreSQL ready
  -> Keycloak ready
  -> render Kong DB-less trust
  -> start Kong
  -> start MDM with bounded security profile
  -> MDM readiness
  -> missing-token 401
  -> signed operator token -> authorized MDM endpoint
  -> signed reader token -> authorized read endpoint where applicable
  -> cross-domain token -> 403
  -> identity/audit integrity evidence
```

## Current gate status

AMBER — infrastructure and security controls are substantially wired, but the end-to-end positive authorization path is not yet proven.

## Non-negotiable rule

Do not mark the MDM security gate GREEN based on actuator readiness, synthetic authorization shortcuts, suppressed startup exceptions, or contract-only checks.
