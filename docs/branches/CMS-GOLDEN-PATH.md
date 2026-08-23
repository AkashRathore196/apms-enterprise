# CMS Golden Path

**Branch:** `feature/cms-golden-path`
**Baseline:** CMS Detailed Build Engineering & Implementation Baseline v1.0
**Runtime gate:** MDM Golden Path acceptance must be GREEN before CMS runtime implementation is enabled.

## Structure

- `domains/cms/` — CMS domain implementation boundary.
- `contracts/cms/` — CMS API and event contracts.
- `quality/cms/` — CMS acceptance and quality evidence.
- `infrastructure/cms/` — CMS runtime and deployment definitions.
- `docs/architecture/cms/` — CMS architecture and implementation baselines.

## Golden Path

API Request → Authentication → Authorization → Validation → Domain Rules → Transaction → CMS State → Audit → Outbox → Commit → Publisher → Kafka → Consumer → Durable Idempotency → Business Outcome → Observability

## Separation rule

CMS must not place Party/Organization master data in its own database or directly access the MDM database. CMS integrates with MDM through governed contracts.

## Current status

Baseline-ready. Runtime implementation remains intentionally gated by MDM acceptance.
