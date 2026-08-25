# MDM Party Identifier Implementation Baseline v1.0

## Scope
This slice implements the approved Party Identifier semantic baseline without expanding taxonomy or introducing Relationship runtime behavior.

## Approved semantic contract

### Ownership
MDM owns the authoritative Party-to-Identifier association and identifier lifecycle. `identifierType`, `issuingAuthority`, and `issuingJurisdiction` are governed reference data; production values are not free text.

### Logical attributes
- `identifierId` — immutable identifier record key
- `partyId` — immutable owning Party
- `identifierType` — governed identifier classification
- `identifierValue` — supplied/display representation when required for provenance/audit
- `normalizedIdentifierValue` — canonical value used for matching and uniqueness
- `issuingAuthority` — conditional governed authority
- `issuingJurisdiction` — conditional governed jurisdiction
- `sourceSystem` — conditional provenance for externally sourced identifiers
- `validFrom` — conditional start of validity
- `validTo` — conditional end of validity
- `isPrimary` — primary designation within the approved type/context scope
- `lifecycleState` — `ACTIVE | SUSPENDED | EXPIRED | RETIRED`
- `version` — optimistic concurrency control
- `createdAt`, `updatedAt` — audit timestamps

### Uniqueness
Default governed identity is `(identifierType, normalizedIdentifierValue, issuingAuthority, issuingJurisdiction)` with authority/jurisdiction nullable only where the identifier type does not require them. Enterprise-generated identifiers may use a type-specific uniqueness rule without issuing authority. Active/current duplicates are prohibited; historical records remain auditable.

### Validation
- identifierId and partyId are immutable after creation.
- identifierType must be a governed value.
- normalizedIdentifierValue is deterministic for the identifier type.
- `validTo >= validFrom` when both are present.
- External identifiers retain required source provenance.
- At most one active primary identifier exists for a Party within the defined identifier type/context.
- Retired/expired identifiers are auditable and are not silently deleted.
- Reactivation is not universal; no generic reactivation transition is added in this slice.

## Physical implementation contract
The implementation must derive, not reinterpret, these semantics into:

- PostgreSQL table and indexes
- Flyway migration
- Java entity/value/validation model
- repository/service boundary
- REST/OpenAPI contract
- audit/history behavior
- relevant event contract
- executable unit and PostgreSQL integration tests

## Explicit exclusions
- No new identifier taxonomy beyond governed reference-data placeholders.
- No speculative jurisdiction catalog.
- No Relationship runtime implementation.
- No Keycloak/Kong security acceptance changes.
- No search projection unless required by an already approved contract.

## Acceptance boundary
The implementation PR is accepted only when build, regression, PostgreSQL persistence, lifecycle validation, audit behavior, and affected eventing tests are green. Security acceptance remains a separate deferred gate.
