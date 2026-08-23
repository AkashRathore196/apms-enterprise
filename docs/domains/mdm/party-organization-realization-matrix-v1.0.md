# MDM Party/Organization Realization Matrix v1.0

## Purpose

Reconcile the approved MDM Party/Organization domain realization against the current implementation baseline and identify the next modeling/engineering work without inventing unapproved semantics.

## Current implementation observed

| Area | Current implementation | Assessment |
|---|---|---|
| Party identity | `partyId` UUID | IMPLEMENTED |
| Party classification | `partyType` = PERSON / ORGANIZATION | IMPLEMENTED; taxonomy requires business confirmation |
| Party lifecycle | DRAFT, SUBMITTED, REVIEW, APPROVED, ACTIVE, SUSPENDED, RETIRED | IMPLEMENTED in code; transitions require reconciliation |
| Party status | `status` string | IMPLEMENTED but semantically duplicated with lifecycle and requires resolution |
| Party version | optimistic-lock `version` | IMPLEMENTED |
| Party created timestamp | `created_at` | IMPLEMENTED |
| Organization identity | `organizationId` UUID | IMPLEMENTED |
| Organization-party linkage | 1:1 `party_id` | IMPLEMENTED |
| Organization legal name | `legalName` | IMPLEMENTED |
| Organization display name | `displayName` | IMPLEMENTED |
| Party relationships | none identified in current Party package | MISSING |
| Party identifiers | no enterprise/external identifier model identified | MISSING |
| Address model | none identified in current Party model | MISSING / BOUNDARY TO RECONCILE |
| Contact/communication model | none identified in current Party model | MISSING / BOUNDARY TO RECONCILE |
| Organization type/classification | no dedicated model identified | MISSING |
| Effective dating | not modeled in Party/Organization entities | MISSING |
| Survivorship/merge | no model identified | MISSING |
| Source provenance | no model identified | MISSING |
| Search projection | not represented in Party/Organization entities | MISSING / INTEGRATION TO RECONCILE |
| Audit | enterprise audit infrastructure exists separately | RECONCILE FIELD/ENTITY COVERAGE |
| Outbox | enterprise/domain outbox infrastructure exists | RECONCILE EVENT OWNERSHIP |

## Immediate semantic issues

1. `status` and `lifecycleState` overlap and must be reconciled before expanding the model.
2. `LifecycleState.APPROVED` exists, while `Party.approve()` moves directly to `ACTIVE`; the business lifecycle therefore needs explicit transition semantics.
3. `PartyType` currently has PERSON and ORGANIZATION only; future classifications must be driven by business requirements rather than technical convenience.
4. Organization is modeled as a 1:1 specialization of Party; confirm whether this remains the authoritative conceptual model for all organization variants.
5. Identifier, relationship, address, communication, provenance and effective-dating semantics are not yet represented at the domain-model level.

## Required next modeling work

### A. Business semantics
- define Party versus Organization meaning
- define organization classifications
- define lifecycle state meanings and permitted transitions
- define duplicate/merge and survivorship semantics
- define external-source provenance

### B. Core information objects
- Party
- Organization
- PartyIdentifier
- PartyRelationship
- OrganizationClassification
- PartyAddress / address representation
- PartyCommunication / communication representation
- lifecycle history
- source provenance

### C. Attribute-level specification
For each approved object define canonical name, business definition, datatype, cardinality, requiredness, authority, validation, effective dating, security classification, API exposure, event exposure, persistence mapping, audit behavior and search behavior.

### D. Engineering reconciliation
Map each approved object/attribute to:
- Java entity/domain representation
- PostgreSQL/Flyway representation
- REST/OpenAPI contract
- Kafka event contract
- audit representation
- search projection
- automated test evidence

## Explicit non-decisions

The following are intentionally NOT invented in this matrix:
- tax identifier taxonomy
- registration identifier taxonomy
- address/contact substructure
- organization hierarchy semantics
- merge survivorship rules
- regulatory classifications

These require business/information-model reconciliation first.

## Current conclusion

The existing MDM Party/Organization implementation is a valid technical seed for the Golden Path, but it is not yet a complete domain model. The next work is semantic and attribute-level realization, followed by controlled schema/API/event evolution.
