# MDM Party/Organization Attribute Matrix v1.0

## Status

**Working domain model — implementation reconciliation**

This matrix separates what is currently implemented from what is required to make Party/Organization implementation-ready. It deliberately does not invent business semantics where the enterprise model has not yet been approved.

## 1. Party core

| Business concept | Canonical attribute | Current implementation | Target status | Notes |
|---|---|---|---|---|
| Party identity | `partyId` | UUID primary key | Implemented | Canonical MDM identifier |
| Party kind | `partyType` | PERSON / ORGANIZATION enum | Partially implemented | Taxonomy must be reconciled with enterprise reference data |
| Lifecycle | `lifecycleState` | DRAFT/SUBMITTED/REVIEW/APPROVED/ACTIVE/SUSPENDED/RETIRED | Partially implemented | Transition matrix still needs formal business approval |
| Operational status | `status` | String ACTIVE | Reconcile | Overlaps conceptually with lifecycle; semantic role must be resolved |
| Record version | `version` | JPA optimistic version | Implemented | Confirm API/event concurrency semantics |
| Created timestamp | `createdAt` | DB default | Implemented | Audit/temporal model needs reconciliation |
| Updated timestamp | `updatedAt` | Not currently evident | Missing | Required decision for change tracking |
| Effective start | `effectiveFrom` | Not currently evident | Missing | Required if master data is effective-dated |
| Effective end | `effectiveTo` | Not currently evident | Missing | Required if master data supports end-dating |

## 2. Organization specialization

| Business concept | Canonical attribute | Current implementation | Target status | Notes |
|---|---|---|---|---|
| Organization identity | `organizationId` | Separate UUID | Reconcile | Determine whether Party ID is the sole enterprise identity or specialization has a technical key |
| Party link | `partyId` | One-to-one FK | Implemented | MDM relationship to Party |
| Legal name | `legalName` | VARCHAR(300), required | Implemented | Business definition and uniqueness rules still require approval |
| Display name | `displayName` | VARCHAR(300), optional | Implemented | Define semantics vs legal/trading name |
| Organization type | `organizationType` | Not evident | Missing | Needs governed taxonomy/reference data |
| Legal form | `legalForm` | Not evident | Missing | Determine business requirement |
| Registration identifier | `registrationId` | Not evident | Missing | Needs identifier model rather than arbitrary column |
| Tax identifier | `taxIdentifier` | Not evident | Missing | Needs identifier type, country, issuer and validation semantics |
| Country of registration | `registrationCountry` | Not evident | Missing | Reference-data dependency |
| Source provenance | `sourceSystem` / provenance | Not evident | Missing | Required if MDM consolidates external sources |

## 3. Identifiers

The next design step should introduce a governed identifier model instead of adding one-off identifier columns.

Required conceptual properties:

- identifier type
- identifier value
- issuing authority/source
- country/jurisdiction where applicable
- primary/preferred indicator
- validity period
- verification status
- provenance

Potential identifiers include enterprise-generated party ID, legal registration number, tax identifier, and external/source-system identifiers. Exact taxonomy remains a business/data-governance decision.

## 4. Relationships

Party/Organization relationships are not yet represented in the current seed implementation.

The domain model should explicitly define:

- relationship type
- source party
- target party
- role semantics
- cardinality
- effective period
- lifecycle/status
- provenance
- audit history

Examples such as parent/subsidiary, legal owner, customer relationship, or contact relationship must be validated against the approved business capability model before implementation.

## 5. Lifecycle reconciliation

Current code contains lifecycle values, but the semantic state machine is incomplete.

The following needs explicit approval:

- valid transitions from each state
- who can trigger each transition
- whether `APPROVED` and `ACTIVE` are distinct business states
- whether `status` is retained as an operational status separate from lifecycle
- suspension/reactivation semantics
- retirement semantics
- effective dating interaction with lifecycle
- merge/survivorship interaction with lifecycle

## 6. Business rules to define

- required attributes by Party type
- uniqueness rules
- identifier uniqueness and verification
- legal/display naming rules
- state transition rules
- effective-date constraints
- relationship validity rules
- duplicate detection criteria
- merge/survivorship rules
- source precedence/provenance
- authorization by lifecycle transition

## 7. Traceability targets

Every final attribute must map to:

**Business definition → logical attribute → physical column/property → API schema → event schema (if exposed) → validation → test → audit/search behavior**

## 8. Current reconciliation conclusion

The current MDM implementation is a valid technical seed for Party/Organization, but not yet a complete canonical master-data model. The next implementation step is to resolve business semantics for identifiers, relationships, organization classification, lifecycle/status, temporal behavior and provenance before adding schema breadth.
