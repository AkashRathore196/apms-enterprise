# MDM Authorization Acceptance Baseline v1.0

## Status

Authentication acceptance is frozen as GREEN on `main`.

## Authorization acceptance boundary

Authorization is a separate gate from authentication. It must prove, through the actual Keycloak/Kong/MDM runtime path:

1. an authenticated identity with the approved MDM authority receives 2xx from the protected MDM probe;
2. an authenticated identity without the required authority receives 403;
3. identity/principal evidence is derived from the verified JWT security context;
4. no authorization assertion depends on caller-supplied payload fields.

## Execution rule

Do not modify the frozen authentication workflow for authorization testing. Authorization acceptance must be implemented as a separate bounded slice with its own fixtures and evidence.

## Expected matrix

| Case | Expected |
|---|---:|
| Missing token | 401 |
| Approved authenticated service identity | 2xx |
| Authenticated identity without required authority | 403 |

## Freeze rule

A failing authorization run must not trigger changes to Keycloak startup, Kong signature trust, or MDM JWT decoder configuration unless the evidence proves an authentication defect.
