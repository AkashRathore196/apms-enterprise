# APMS MDM Acceptance Gate v1.0

**Status:** Active acceptance checklist  
**Capability:** Master Data Management (MDM)  
**Purpose:** Drive the remaining MDM Golden Path evidence to closure before CMS implementation starts.

## Acceptance gates

| Gate | Status | Evidence target |
|---|---|---|
| PostgreSQL + Kafka integration | OPEN | Executable integration path proves persisted state, publication and consumption |
| Keycloak/Kong authorization | OPEN | Protected API matrix proves authentication and authorization boundaries |
| Camunda approval execution | OPEN | Approval flow executes through EWOP integration |
| OpenSearch projection/rebuild | OPEN | Projection creation and deterministic rebuild validated |
| Trace/correlation propagation | OPEN | Request-to-event correlation visible across service boundaries |
| Failure injection/recovery | OPEN | Defined failures recover without violating business or event consistency |
| Backup/restore reconciliation | OPEN | Restored database reconciles with accepted MDM state expectations |
| Multi-replica outbox concurrency | OPEN | Concurrent publishers preserve exactly-once business effect / durable idempotency |

## Sequencing rule

CMS may remain architecture- and baseline-ready, but CMS runtime implementation must remain gated until all mandatory MDM acceptance gates are GREEN.

## Exit criterion

MDM moves from **AMBER / PENDING** to **GREEN / ACCEPTED** only when all mandatory gates have executable evidence recorded in GitHub and the implementation is reconciled against the MDM detailed engineering baseline.
