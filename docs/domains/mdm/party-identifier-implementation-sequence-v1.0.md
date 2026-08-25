# MDM Party Identifier Implementation Sequence v1.0

## Approved implementation order

1. Domain entity and lifecycle invariants.
2. PostgreSQL/Flyway persistence and governed uniqueness indexes.
3. Repository/service transaction boundary.
4. Audit and outbox event emission.
5. REST/OpenAPI resource contract.
6. Versioned Kafka event contracts.
7. Unit, persistence, API, and event acceptance tests.
8. Authorized identifier search projection only after search semantics are separately approved.

## Current checkpoint

- Decision baseline: approved in Issue #17.
- Domain/persistence foundation: implemented on the Party Identifier realization branch.
- REST/event contract: documented in `party-identifier-api-event-contract-v1.0.md`.
- Runtime REST controller and generated OpenAPI changes remain the next implementation step.

## Gates

The Party Identifier slice must keep passing MDM Build, Regression, Outbox/Kafka, durable idempotency, and Eventing E2E checks.

Keycloak/Kong Security Acceptance remains a separate deferred gate and is not a prerequisite for this slice.

## Scope exclusions

No additional identifier taxonomy values, Relationship runtime implementation, multiple primary contexts, or arbitrary identifier search semantics are introduced in v1.0.
