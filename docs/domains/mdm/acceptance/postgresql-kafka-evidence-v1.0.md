# MDM PostgreSQL + Kafka Acceptance Evidence v1.0

## Purpose

Record the executable evidence boundary for the MDM Party/Organization Golden Path from authoritative PostgreSQL state through outbox publication, Kafka transport, durable consumer processing and idempotency.

## Evidence status

- MDM Build: green on latest PR execution.
- MDM Regression: green on latest PR execution.
- Outbox Publisher Acceptance: green on latest PR execution.
- Outbox/Kafka Acceptance: green on latest PR execution.
- Eventing End-to-End Acceptance: green on latest PR execution.
- Processed Event Idempotency Acceptance: green on latest PR execution.

## Scope

This evidence confirms the executable CI path for:

PostgreSQL transaction/state → outbox → publisher → Kafka → consumer → processed-event durability/idempotency.

## Remaining distinction

This evidence does not close the broader MDM Golden Path acceptance register. Keycloak/Kong authorization, Camunda approval execution, OpenSearch projection/rebuild, trace/correlation propagation, failure injection/recovery, backup/restore reconciliation, and multi-replica outbox concurrency remain separate acceptance gates.

## Acceptance rule

Only executable GitHub Actions evidence is used to promote a gate. Documentation alone does not make a gate green.
