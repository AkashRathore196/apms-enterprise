# MDM Party Identifier Decision Baseline v1.0

## Status

Decision baseline prepared on 2026-08-24. Production persistence remains gated until the identifier taxonomy and this decision baseline are formally accepted.

## 1. Ownership and taxonomy

MDM owns the authoritative Party-to-Identifier association and identifier lifecycle. `identifierType` is governed reference data owned through enterprise/MDM reference-data governance; production values are not free text.

The initial taxonomy is intentionally constrained to approved identifier classes only. Enterprise-generated identifiers and externally issued identifiers are distinct semantic classes. New identifier types require governed approval before implementation or persistence rules are added.

## 2. Normalization

Normalization is deterministic and identifier-type specific. The normalized canonical value is used for matching and uniqueness. A supplied/display representation may be retained when required for audit or provenance. Generic normalization is restricted to transformations that are safe across the governed type; type-specific formatting, checksum, and validation rules belong to the identifier-type definition.

## 3. Uniqueness

No universal global uniqueness rule is assumed. The default governed identity combination is:

`(identifierType, normalizedIdentifierValue, issuingAuthority, issuingJurisdiction)`

Authority and jurisdiction participate only where required by the identifier type and must be represented consistently when absent. Enterprise-generated identifiers may use a type-specific uniqueness rule without an issuing authority. Active/current duplicate identifiers are prohibited; historical records remain auditable.

## 4. Issuing authority and jurisdiction

`issuingAuthority` is governed reference data where applicable. `issuingJurisdiction` is a governed geographic/jurisdiction reference, not free text. Both are conditional on identifier type. Their combination participates in uniqueness only where the identifier type requires it.

## 5. Identifier lifecycle

Identifier lifecycle is independent from Party lifecycle. The baseline states are:

`ACTIVE | SUSPENDED | EXPIRED | RETIRED`

Creation enters `ACTIVE` unless a governed identifier type explicitly requires a pending verification state. `EXPIRED` is validity-driven; `RETIRED` is an explicit business retirement. Silent deletion is prohibited. Reactivation is not a universal transition and requires type-specific governance.

## 6. Primary designation

`isPrimary` is scoped by an explicit identifier context rather than globally per Party. At most one active primary identifier exists for a Party within a defined `(identifierType, context)` scope. The context must be explicit in the implementation contract before persistence if multiple contexts are required.

## 7. Provenance

Externally sourced identifiers retain `sourceSystem` and sufficient source provenance to trace origin. MDM remains authoritative for the Party-to-Identifier association and lifecycle; source systems remain authoritative for their own source representation.

## 8. Temporal validity

`validFrom` is optional unless required by identifier type. `validTo` must not precede `validFrom`. Active identifiers must satisfy the governed validity policy. Expiry derived from validity remains auditable.

## 9. Implementation gate

After formal acceptance of this decision baseline, the implementation slice may derive:

- PostgreSQL table, indexes, and uniqueness constraints
- Flyway migration
- Java entity/domain model and validation
- REST/OpenAPI contracts
- Kafka event contracts
- audit/history behavior
- executable unit and persistence tests
- authorized identifier search projection

No taxonomy-specific production persistence should be introduced before the required identifier-type decisions are approved.
