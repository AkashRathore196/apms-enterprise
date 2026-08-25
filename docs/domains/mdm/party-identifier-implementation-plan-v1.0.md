# MDM Party Identifier Implementation Plan v1.0

## Purpose

Translate the approved Issue #17 semantics into a bounded implementation sequence without introducing speculative identifier taxonomy.

## Scope

This slice covers the Party Identifier first-class entity and its governed persistence/validation boundary. Relationship runtime work, Keycloak/Kong security work, and new identifier types remain out of scope.

## Build order

1. Domain value model
   - Identifier lifecycle: ACTIVE, SUSPENDED, EXPIRED, RETIRED
   - immutable identifierId and partyId
   - normalized value as canonical matching value
   - supplied/display value only where provenance/audit requires it

2. PostgreSQL/Flyway
   - party_identifier table
   - foreign key to party
   - controlled type/authority/jurisdiction references at the approved abstraction level
   - validity check: validTo >= validFrom when both are present
   - indexes for Party lookup and governed uniqueness
   - no speculative type-specific constraints

3. Java persistence/domain
   - JPA entity and repository
   - optimistic versioning
   - lifecycle transition methods
   - type-aware normalization boundary
   - primary designation validation boundary

4. Service/audit
   - create, update, suspend, expire, retire operations
   - MDM-owned Party association
   - audit/history for lifecycle and primary changes
   - provenance retention for externally sourced identifiers

5. API/event contract
   - REST/OpenAPI schemas derived from the entity contract
   - versioned identifier lifecycle/domain events where required
   - no event fields beyond approved semantics

6. Tests
   - unit validation for lifecycle and temporal rules
   - persistence tests for FK, uniqueness, and validity constraints
   - primary identifier scope tests
   - provenance tests
   - regression coverage for existing Party lifecycle

## Merge gate

The implementation PR must demonstrate:

- approved semantic decisions are implemented without taxonomy creep
- Flyway applies cleanly on a new PostgreSQL schema
- persistence constraints match the approved uniqueness model
- lifecycle and temporal rules are executable and tested
- existing MDM Build/Regression/Eventing gates remain green
- Security Acceptance remains separate and deferred
