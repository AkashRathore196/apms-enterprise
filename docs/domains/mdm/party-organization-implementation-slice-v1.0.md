# MDM Party/Organization Implementation Slice v1.0

## Purpose
Translate the canonical Party/Organization model into the first controlled implementation slice without prematurely finalizing unresolved taxonomies.

## Slice 1 — Canonical lifecycle cleanup

### Required changes
- Treat `lifecycleState` as the only business lifecycle state.
- Remove `status` as a competing business lifecycle concept through a controlled migration/change; retain it only if a non-business technical purpose is explicitly approved.
- Add `updatedAt` as a system-managed record timestamp.
- Preserve optimistic `version` semantics.
- Enforce legal lifecycle transitions in the domain service rather than arbitrary field mutation.

### Controlled transition rules
- DRAFT → SUBMITTED
- SUBMITTED → REVIEW
- REVIEW → APPROVED
- REVIEW → DRAFT
- APPROVED → ACTIVE
- ACTIVE → SUSPENDED
- SUSPENDED → ACTIVE
- ACTIVE → RETIRED

Approval itself is not equivalent to business-effective activation.

## Slice 2 — Core Party persistence

The first persistence evolution should introduce only canonical, approved technical attributes:

| Field | Purpose | Type | Notes |
|---|---|---|---|
| party_id | Immutable enterprise identifier | UUID | Existing |
| party_type | Person/Organization classification | VARCHAR/enum | Existing |
| lifecycle_state | Canonical business lifecycle | VARCHAR/enum | Existing |
| version | Optimistic concurrency | BIGINT | Existing |
| created_at | Creation timestamp | TIMESTAMPTZ | Existing |
| updated_at | Modification timestamp | TIMESTAMPTZ | New |

Effective dates and provenance remain conditional until object-level policies are approved.

## Slice 3 — Organization persistence

Retain current specialization boundary:

- organization_id
- party_id (unique foreign key to Party)
- legal_name
- display_name

Do not add `organization_type` or registration fields until the relevant taxonomies and jurisdiction semantics are approved.

## Slice 4 — First-class identifiers

Introduce a Party Identifier model only after the identifier taxonomy/uniqueness decision is approved.

Required conceptual contract:

identifierId → partyId → identifierType → identifierValue → authority/jurisdiction → provenance → validity → primary/status semantics.

Identifier values must not be treated as globally unique without approved type/authority semantics.

## Slice 5 — First-class relationships

Introduce Party Relationship as a separate model only for relationships whose role, validity, lifecycle, auditability or direction makes them business objects.

Required conceptual contract:

relationshipId → fromPartyId → toPartyId → relationshipType → role → validity → lifecycle → provenance → version.

## Slice 6 — API/event mapping

Every implemented attribute must map explicitly to:

- REST request/response schema
- Kafka event envelope/payload where applicable
- validation rules
- audit behavior
- search projection where applicable
- acceptance evidence

No persistence-only business fields are permitted without a defined semantic role.

## Slice 7 — Reconciliation gate

Before merging implementation changes, compare:

1. canonical model
2. physical model
3. Java model
4. Flyway schema
5. REST/OpenAPI contracts
6. Kafka event contracts
7. unit/integration/acceptance tests

Classify each modeled element as:

- aligned
- missing
- inconsistent
- intentionally deferred

## Explicit deferrals

The following remain outside this implementation slice until their semantic decisions are approved:

- organizationType taxonomy
- full identifier taxonomy
- relationship taxonomy and role semantics
- effective-dating policy beyond basic timestamp integrity
- merge/survivorship mechanics
- address/contact model
- field-level security classification
- exact approval workflow authority

## Definition of done

This slice is complete when canonical lifecycle semantics and system-managed timestamps are implemented consistently across Java, PostgreSQL/Flyway, API/event mappings and tests, with no competing business `status` concept and with explicitly documented deferrals.
