# MDM Party Identifier Normalization Contract v1.0

## Purpose

Define the implementation-safe normalization boundary for Party identifiers without fixing identifier-type-specific business rules before Issue #17 is approved.

## Contract

Every Party Identifier must preserve the original business value and, where normalization is defined for its identifier type, derive a canonical comparison value used for validation and uniqueness enforcement.

### Logical fields

| Field | Purpose |
|---|---|
| identifierValue | Original supplied identifier value retained for governed display/audit needs |
| normalizedValue | Canonical comparison value derived by the identifier-type normalization policy |
| normalizationVersion | Version of the normalization rule applied |
| identifierType | Approved reference-data classification |
| issuingAuthority | Authority dimension when applicable |
| issuingJurisdiction | Jurisdiction dimension when applicable |

## Safe baseline rules

Until identifier-type rules are approved:

1. Do not silently alter the submitted identifier value.
2. Do not assume universal upper/lower-casing, whitespace removal, punctuation stripping, Unicode normalization, or checksum transformation.
3. Do not assume one global uniqueness constraint.
4. Store enough provenance to reproduce the canonical comparison result.
5. Type-specific normalization rules must be explicit, versioned, testable, and deterministic.
6. Changing a normalization rule must be treated as a governed model/version change; it must not silently reinterpret historical identifiers.

## Uniqueness derivation

The persistence constraint must be derived from the approved identifier taxonomy. Candidate uniqueness dimensions are:

`identifierType + normalizedValue + issuingAuthority + issuingJurisdiction`

The final combination is not approved by this artifact.

## Validation boundary

The domain layer validates semantic rules; persistence enforces only constraints that have been approved and encoded in the canonical model.

## Traceability

Once Issue #17 is approved, this contract will map to:

- identifier Java value object/entity
- PostgreSQL columns/indexes
- Flyway migration
- REST/OpenAPI request validation
- Kafka event representation
- unit/property tests
- audit/provenance

## Status

Implementation-safe baseline. Identifier-specific rules remain gated by the Party Identifier taxonomy and uniqueness decision.
