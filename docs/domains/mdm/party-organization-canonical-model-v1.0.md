# MDM Party/Organization Canonical Domain Model v1.0

## Purpose

Define the canonical business and information model that governs Party/Organization implementation. This is the semantic source of truth; persistence and APIs must conform to it.

## 1. Core concepts

### Party
A uniquely identified enterprise master subject that may represent a person or an organization. MDM owns identity/master-data facts about the Party.

### Organization
A Party specialization representing an organization. Organization-specific attributes belong to the Organization specialization and do not duplicate Party identity attributes.

### Party Identifier
A governed identifier assigned to or associated with a Party, including enterprise and externally sourced identifiers. Identifier type, value, authority/source, validity and uniqueness semantics must be explicit.

### Party Relationship
A typed relationship between two Party records with a defined role, direction, validity period and lifecycle.

## 2. Canonical Party attributes

| Attribute | Semantic role | Requiredness | Notes |
|---|---|---|---|
| partyId | Enterprise immutable identifier | Required | UUID currently implemented |
| partyType | Person/Organization classification | Required | Current enum: PERSON, ORGANIZATION |
| lifecycleState | Master-record lifecycle | Required | Canonical lifecycle; supersedes generic status for business meaning |
| version | Optimistic concurrency/version | Required | Technical control attribute |
| createdAt | Record creation timestamp | Required | System-managed |
| updatedAt | Last modification timestamp | Required | System-managed; must be added to canonical persistence if absent |
| effectiveFrom | Business-effective start | Conditional | Required where temporal validity applies |
| effectiveTo | Business-effective end | Conditional | Must be >= effectiveFrom when present |
| sourceSystem | Provenance of originating source | Conditional | Required when record originates from external source |
| sourceRecordId | External/source identifier | Conditional | Pair with sourceSystem when external provenance is tracked |

## 3. Canonical Organization attributes

| Attribute | Semantic role | Requiredness |
|---|---|---|
| organizationId | Organization specialization identifier | Required |
| partyId | Link to Party | Required; unique |
| legalName | Registered/legal organization name | Required |
| displayName | Operational/display organization name | Optional |
| organizationType | Business classification of organization | Required once taxonomy is approved |
| registrationIdentifier | Government/statutory/business registration identifier | Conditional |
| registrationCountry | Jurisdiction for registration identifier | Conditional |
| registrationDate | Registration date | Conditional |
| effectiveFrom | Business-effective start | Conditional |
| effectiveTo | Business-effective end | Conditional |

## 4. Canonical identifier model

A Party may have zero or more identifiers.

Minimum conceptual fields:

- identifierId
- partyId
- identifierType
- identifierValue
- issuingAuthority
- issuingCountry/jurisdiction
- sourceSystem
- validFrom
- validTo
- isPrimary
- status

Uniqueness is governed by identifier type, authority/jurisdiction and value semantics; no single global uniqueness rule is assumed.

## 5. Canonical relationship model

A Party relationship must be modeled as a first-class object when the relationship has its own lifecycle, validity, role or audit requirements.

Minimum conceptual fields:

- relationshipId
- fromPartyId
- toPartyId
- relationshipType
- relationshipRole
- validFrom
- validTo
- lifecycleState
- sourceSystem
- version

Examples include organizational hierarchy, ownership/control, employment/association, and other approved enterprise relationship types.

## 6. Lifecycle model

Use `lifecycleState` as the canonical business lifecycle. The generic `status` field must not remain a competing business state.

Proposed controlled states:

DRAFT → SUBMITTED → REVIEW → APPROVED → ACTIVE

ACTIVE → SUSPENDED
SUSPENDED → ACTIVE
ACTIVE → RETIRED

REVIEW → DRAFT
SUBMITTED → DRAFT

`APPROVED` represents approval of the master record; `ACTIVE` represents business-effective availability for downstream use. Transition policy must be enforced by domain rules and approval integration.

## 7. Domain rules

1. partyId and organizationId are immutable after creation.
2. Organization must reference exactly one Party of type ORGANIZATION.
3. Person-specific attributes must not be stored on Organization.
4. Organization-specific attributes must not be duplicated on Party.
5. Lifecycle transitions are explicit and validated; arbitrary state mutation is prohibited.
6. Effective dates cannot create contradictory validity periods.
7. Identifier uniqueness follows identifier-type/authority semantics.
8. External provenance is retained where an external source creates or updates master data.
9. Merge/survivorship must preserve auditability and identifier lineage.
10. Cross-domain systems reference MDM Party identity and do not create competing master records.

## 8. Engineering mapping requirements

The canonical model must map to:

- PostgreSQL tables and columns via Flyway
- Java entities/value objects/enums
- REST/OpenAPI request and response schemas
- Kafka event payloads
- validation rules and tests
- audit records
- search projections
- security/data classification rules

## 9. Explicitly unresolved decisions

The following remain controlled design decisions and must be approved before production freeze:

- organizationType taxonomy
- full identifier taxonomy and uniqueness rules
- relationship taxonomy and role semantics
- temporal/effective-dating policy by object
- merge/survivorship mechanics
- address/contact ownership and modeling boundary
- field-level security classification
- exact approval workflow semantics and authority

## 10. Implementation rule

Do not add database fields merely because they are listed here. Every field must be reconciled to an approved business or information requirement, mapped to its API/event exposure, and covered by validation/test evidence before implementation is frozen.
