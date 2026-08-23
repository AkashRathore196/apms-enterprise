# MDM Eventing Integration Branch

This branch isolates Kafka/eventing integration experiments from the known-good main regression baseline.

Main must remain the stable MDM Party/Organization regression baseline until eventing tests are bounded and green.

Eventing branch test stages:
1. Kafka-only transport test with strict timeout.
2. PostgreSQL + Spring integration test with DynamicPropertySource.
3. Outbox publication test.
4. Durable processed_event idempotency test.
5. End-to-end Outbox -> Kafka -> consumer -> PostgreSQL test.

No eventing test may allow an unbounded wait. Container startup, broker readiness, producer send, consumer poll, and database connection must have explicit timeouts.
