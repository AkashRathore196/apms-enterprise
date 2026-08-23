# MDM Security Acceptance Target v1.0

## Purpose

Define the bounded application target for Keycloak/Kong/MDM authorization acceptance without using actuator health as the positive authorization test.

## Acceptance layers

1. **Readiness**
   - `/actuator/health` proves the service is alive.
   - It is not authorization evidence.

2. **Gateway authentication**
   - Missing/invalid JWT must be rejected by Kong with HTTP 401.

3. **Application authentication**
   - A valid JWT presented through Kong must establish an authenticated Spring Security principal.

4. **Application authorization**
   - An MDM-authorized role must access a protected MDM endpoint.
   - A caller without MDM authority must receive HTTP 403.

5. **Identity integrity**
   - Authorization and audit actor identity must derive from the verified JWT principal, not request payload fields.

## Protected test endpoint

The acceptance target is a dedicated, non-business-impacting diagnostic endpoint under the MDM API boundary. It must:

- require authentication;
- require the governed MDM role/scope;
- return a small deterministic response;
- expose the authenticated subject/authorities only as test evidence, without mutating domain data;
- remain disabled or non-routable outside test/acceptance profiles if the enterprise security baseline requires it.

## Required evidence

| Scenario | Expected |
|---|---|
| No token | 401 |
| Invalid signature | 401 |
| Expired token | 401 |
| Valid MDM operator | 2xx |
| Valid MDM reader | 2xx |
| Valid cross-domain caller | 403 |
| Caller-supplied actor differs from JWT subject | JWT subject remains authoritative |

## Status

OPEN. The target is defined; the concrete endpoint and end-to-end runtime evidence remain to be implemented.
