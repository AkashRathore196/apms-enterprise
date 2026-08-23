# MDM Eventing Integration Stages

This branch is intentionally isolated from `main`.

## Stage 1 — Kafka transport
- One Kafka broker container only.
- No Spring Boot application context.
- Explicit broker readiness timeout: 60s.
- Producer metadata/send timeout: 10s.
- Consumer poll timeout: 10s.
- Overall test timeout: 2m.

## Stage 2 — PostgreSQL/Spring
- PostgreSQL Testcontainer only.
- `@DynamicPropertySource` supplies runtime datasource properties.
- Explicit database startup timeout: 60s.
- Spring context timeout is bounded by the Maven/JUnit test timeout.

## Stage 3 — Outbox publication
- Persist one outbox event.
- Claim/lock the event.
- Publish to Kafka.
- Mark `PUBLISHED` only after broker acknowledgement.
- Retry remains bounded and observable.

## Stage 4 — Durable idempotency
- Deliver the same event twice.
- Persist exactly one `processed_event` record per `(event_id, consumer_group)`.
- Duplicate delivery must produce no second business effect.

## Stage 5 — End-to-end
`Party approval -> outbox -> Kafka -> consumer -> processed_event`.

No stage should be merged into `main` until its CI result is green and bounded.
