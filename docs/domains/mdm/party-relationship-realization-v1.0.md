# MDM Party Relationship Realization v1.0

## Purpose

Define the controlled implementation contract for Party relationships as a first-class MDM concept when a relationship has its own role, lifecycle, validity, provenance or audit needs.

## Logical model

| Attribute | Requiredness | Meaning |
|---|---|---|
| relationshipId | Required | Immutable relationship identifier |
| fromPartyId | Required | Source/subject Party |
| toPartyId | Required | Target Party |
| relationshipType | Required | Controlled relationship classification |
| relationshipRole | Conditional | Role semantics when required by the relationship type |
| validFrom | Conditional | Business-effective start |
| validTo | Conditional | Business-effective end |
| lifecycleState | Required | Relationship lifecycle |
| sourceSystem | Conditional | Provenance when externally sourced |
| version | Required | Optimistic concurrency control |
| createdAt | Required | Creation timestamp |
| updatedAt | Required | Last modification timestamp |

## Boundary

A relationship is not merely a foreign-key convenience. It becomes a governed business object when its meaning, role, validity, lifecycle, provenance or auditability matters independently of the related Party records.

## Validation rules

1. A relationship must reference two existing Parties.
2. Self-referential relationships are prohibited unless a specific relationship type explicitly permits them.
3. Relationship type must come from approved enterprise/reference taxonomy.
4. validTo must not precede validFrom.
5. Direction and role semantics must be explicit; reverse relationships are not assumed equivalent unless governed as symmetric.
6. Duplicate active relationships must be prevented according to the approved relationship-type uniqueness rule.
7. Relationship history must remain auditable; expired/retired relationships are not silently deleted.
8. Cross-domain systems consume governed Party relationships through APIs/events and do not directly manipulate MDM relationship persistence.

## Examples for future taxonomy approval

- ORGANIZATION_PARENT_OF
- ORGANIZATION_OWNS
- ORGANIZATION_CONTROLS
- PARTY_EMPLOYED_BY
- PARTY_ASSOCIATED_WITH

These are examples only and are not approved taxonomy values.

## Current implementation status

Party relationships are not currently implemented as a first-class persistent model. Taxonomy, role semantics, symmetry/direction and uniqueness policy remain gated design decisions.

## Traceability targets

Approved relationship definitions must map to:

- PostgreSQL table and indexes
- Flyway migration
- Java entity/domain object
- REST/OpenAPI schemas
- Kafka event payloads where relationship changes are published
- validation and lifecycle tests
- audit/history
- authorized search projection
