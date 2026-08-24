# MDM Security Acceptance — Deferral Record v1.0

## Status

**DEFERRED / BLOCKED — not a release blocker for the current implementation sequence.**

## Reason

The MDM Keycloak/Kong security acceptance workflow has reached a stable application/runtime boundary previously proven green, but the current workflow contains CI-only trust-material generation failures in the Kong preparation step. Repeated iterations have produced shell/Python/YAML plumbing defects without changing the underlying MDM security architecture.

## Decision

Pause further changes to the MDM Security Acceptance workflow until the surrounding MDM implementation sequence has progressed enough to justify a dedicated acceptance hardening pass.

Do **not** treat the latest failed workflow as evidence that the previously validated Keycloak → Kong → MDM authentication architecture is invalid. The latest failures are in CI preparation before Kong startup.

## Last Known Evidence

The prior controlled authentication checkpoint passed the protected MDM security probe using a Keycloak-issued token. That checkpoint remains historical evidence only; it is not a claim that the current `main` workflow is green.

## Work Allowed While Deferred

Continue implementation work that does not alter the frozen authentication architecture or depend on this CI gate being green, including MDM domain implementation, database/eventing work, integration work, and separate authorization design.

## Re-entry Criteria

Resume this acceptance gate only when:

1. Kong trust material generation is implemented as a single deterministic, independently testable artifact-generation step.
2. The workflow validates that generated Kong configuration before startup.
3. The authentication contract and workflow validator are synchronized.
4. One clean run demonstrates the full Keycloak-issued JWT → Kong → MDM path without CI scripting workarounds.

## Guardrail

Until re-entry, do not repeatedly rerun or patch this workflow in response to individual CI-only failures. Treat this document as the explicit engineering checkpoint.
