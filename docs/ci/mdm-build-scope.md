# MDM Build CI Scope

## Purpose

Keep the generic MDM build gate deterministic and bounded. PostgreSQL and Kafka runtime integration acceptance remains in dedicated workflows with explicit timeouts.

## Generic build gate

The generic build validates:

- Maven model
- compilation
- deterministic application/unit tests
- packaging

It must not be the aggregate runtime integration acceptance gate.

## Dedicated acceptance gates

Runtime integration coverage is validated separately by the bounded MDM acceptance workflows for PostgreSQL, Kafka smoke, outbox publishing, processed-event idempotency, end-to-end eventing, and regression integration coverage.

## Rationale

A broad `mvn test` invocation can enter long-lived Kafka/Testcontainers integration behavior and consume the entire workflow timeout. Keeping those tests in dedicated acceptance workflows makes failures attributable and preserves a predictable build gate.
