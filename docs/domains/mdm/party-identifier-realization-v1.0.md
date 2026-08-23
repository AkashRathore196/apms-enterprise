# MDM Party Identifier Realization v1.0

## Purpose

Define the controlled implementation contract for Party identifiers without prematurely fixing every identifier taxonomy.

## Canonical meaning

A Party Identifier is a first-class governed identifier associated with exactly one Party. It may be enterprise-generated or externally sourced. Its uniqueness, validity, authority, provenance and primary designation are governed by identifier type and issuing authority semantics.

## Logical model

| Attribute | Requiredness | Meaning |
|---|---|---|
| identifierId | Required | Immutable identifier for the identifier record |
| partyId | Required | Owning MDM Party |
| identifierType | Required | Controlled identifier classification |
| identifierValue | Required | Identifier value, stored according to identifier semantics |
| issuingAuthority | Conditional | Authority that issued/recognizes the identifier |
| issuingJurisdiction | Conditional | Country/state/jurisdiction where authority applies |
| sourceSystem | Conditional | Source system when externally supplied |
| validFrom | Conditional | Start of identifier validity |
| validTo | Conditional | End of identifier validity |
| isPrimary | Required | Whether identifier is primary for its governed type/context |
| lifecycleState | Required | Identifier lifecycle, independent of Party lifecycle |
| version | Required | Optimistic concurrency control |
| createdAt | Required | Creation timestamp |
| updatedAt | Required | Last modification timestamp |

## Ownership

MDM owns the authoritative association and governance of enterprise Party identifiers. Source-system provenance must be retained where the identifier originates outside MDM.

## Uniqueness rule

No universal global uniqueness rule is assumed. Uniqueness must be defined as a governed constraint over the relevant combination of identifier type, value, issuing authority and jurisdiction.

## Validation rules

1. identifierId is immutable.
2. partyId is immutable after creation.
3. identifierType must be an approved reference-data value before production freeze.
4. identifierValue is mandatory and normalized according to identifier type rules.
5. validTo must not precede validFrom.
6. External identifiers require source provenance when the source is known/required.
7. Primary designation must obey one-primary-per-defined-context rules once that context is approved.
8. Retired/expired identifiers remain auditable and are not silently deleted.

## Current implementation status

Party Identifier is not currently implemented as a first-class persistent entity. This artifact therefore defines the target realization and intentionally does not add taxonomy-specific constraints until the identifier taxonomy and uniqueness policy are approved.

## Traceability targets

The approved model must map to:

- PostgreSQL table and indexes
- Flyway migration
- Java entity/value objects
- REST/OpenAPI schemas
- domain validation tests
- audit/history
- relevant Kafka event payloads
- search projection where identifier search is authorized

## Gate

Before implementation merge, approve:

- identifier taxonomy
- normalization rules
- uniqueness combinations
- issuing authority/jurisdiction semantics
- lifecycle states
- primary designation scope
