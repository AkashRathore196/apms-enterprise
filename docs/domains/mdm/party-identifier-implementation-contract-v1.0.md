# MDM Party Identifier Implementation Contract v1.0

## Frozen contract

The Party Identifier implementation is derived from the approved Issue #17 baseline.

### Required fields

- identifierId: immutable UUID
- partyId: immutable UUID reference to Party
- identifierType: governed identifier classification
- identifierValue: supplied/display representation when retained
- normalizedIdentifierValue: canonical matching/uniqueness representation
- issuingAuthority: nullable governed authority reference
- issuingJurisdiction: nullable governed jurisdiction reference
- sourceSystem: nullable provenance source
- validFrom: nullable instant
- validTo: nullable instant
- isPrimary: primary designation within the defined identifier context
- lifecycleState: ACTIVE | SUSPENDED | EXPIRED | RETIRED
- version: optimistic concurrency version
- createdAt: immutable creation timestamp
- updatedAt: modification timestamp

## Constraints

1. identifierId is immutable.
2. partyId is immutable after creation.
3. identifierType is governed; no free-text taxonomy expansion in this slice.
4. normalizedIdentifierValue is mandatory and deterministic.
5. validTo must not precede validFrom.
6. Active/current duplicates are prohibited using the approved governed uniqueness combination.
7. Historical records remain auditable.
8. External identifiers retain source provenance where applicable.
9. Primary designation is enforced only within the approved identifier context; no speculative multi-context model is introduced.
10. Identifier lifecycle is independent of Party lifecycle.

## Implementation exclusions

- no new identifier taxonomy values are invented
- no Relationship runtime implementation
- no Keycloak/Kong changes
- no generic cross-domain search implementation beyond the authorized identifier-search boundary
