# APMS MDM Acceptance Evidence v1.0

## Current gate

**PostgreSQL + Kafka integration acceptance — implementation evidence prepared**

## Existing executable coverage

The repository already contains separate integration tests for:

- PostgreSQL-backed outbox persistence and publisher acknowledgement.
- Real Kafka broker reachability.
- Runtime Kafka publish/consume behavior.
- MDM eventing and regression paths.

These tests are retained as dedicated acceptance coverage rather than being folded into the generic deterministic build gate.

## Required combined proof

The remaining evidence target is one bounded executable path proving the complete MDM eventing chain:

1. Authoritative MDM transaction persists state.
2. Audit and outbox state are committed with the transaction.
3. A real outbox publisher reads the pending event.
4. The publisher delivers the event to a real Kafka broker.
5. The consumer receives the event.
6. Durable idempotency prevents duplicate business effect.
7. The resulting business state remains consistent.

## Acceptance rule

This evidence must execute in a dedicated bounded CI workflow and must not broaden the generic `mvn test` gate.

## Current status

**AMBER — prerequisite test components exist; combined end-to-end executable proof remains to be closed.**
