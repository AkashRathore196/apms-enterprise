# Master Data Management (MDM)

## Status

- Architecture: COMPLETE
- Detailed engineering: COMPLETE
- Party/Organization Golden Path: ADVANCED IMPLEMENTATION
- Golden Path validation: AMBER
- Full MDM acceptance: PENDING

## Golden Path

Party/Organization → validate → duplicate check → submit → approve → activate → PostgreSQL → audit/outbox → Kafka → idempotent consumer → search projection → observability.

## Scope

Party, Organization, Location, Reference Data, Material, Equipment, Resource, lifecycle/governance, data quality, duplicate management, merge/survivorship, audit, APIs, events, search and migration.

## Ownership rule

MDM is the authoritative enterprise master-data capability. Downstream domains consume governed master identities and may maintain projections, but must not create competing master authorities or direct database coupling.
