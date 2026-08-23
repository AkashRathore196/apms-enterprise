# APMS MDM Party/Organization Domain Realization v1.0

## Purpose

Define the implementation-ready domain realization for MDM Party/Organization and establish traceability from business meaning to executable engineering artifacts.

## Realization chain

Business concept → functional behavior → information object → entity → attribute → relationship → identifier → lifecycle → business rule → API → event → persistence → test → acceptance.

## Scope

### Party

The enterprise subject represented by MDM. Party is the conceptual root for individuals and organizations and must remain authoritative for identity/master-data concerns only.

### Organization

An organization-specific specialization of Party representing an enterprise, institution, business unit, or other organization type governed by MDM.

## Required modeling decisions

Before extending runtime implementation, reconcile and explicitly approve:

1. Party type and subtype taxonomy.
2. Organization classification/type taxonomy.
3. Enterprise identifiers and identifier types.
4. Legal name, display name, and naming semantics.
5. Lifecycle states and valid transitions.
6. Effective dating and temporal semantics.
7. Party/Organization relationships and relationship roles.
8. Address and communication/contact ownership boundaries.
9. External identifiers and source-system provenance.
10. Record versioning and optimistic concurrency semantics.
11. Duplicate/merge policy and survivorship ownership.
12. Audit/history requirements.
13. Search/index projection requirements.
14. Security classification and field-level access requirements.

## Attribute specification template

Every authoritative attribute must be specified with at least:

| Property | Required definition |
|---|---|
| Business name | Human/business meaning |
| Technical name | Canonical field/property name |
| Definition | Precise semantic definition |
| Type | Logical and physical type |
| Cardinality | 0..1, 1..1, 0..N, etc. |
| Requiredness | Required/optional/conditional |
| Authority | Owning system/domain |
| Source | Origin/provenance where applicable |
| Validation | Domain constraints |
| Lifecycle | Creation/update/deprecation behavior |
| Effective dating | If applicable |
| Sensitivity | Security/data classification |
| API exposure | Request/response/event exposure |
| Persistence | Table/column mapping |
| Audit | Audit/history behavior |
| Search | Index/projection behavior |

## Engineering traceability

Each modeled object/attribute must be traceable to its implementation representation where applicable:

- Java domain/entity/value object
- PostgreSQL table/column
- Flyway migration
- REST/OpenAPI schema
- Kafka event schema
- validation rule/test
- audit record
- search projection
- acceptance evidence

## Boundary rules

- MDM is authoritative for Party/Organization master data.
- Other domains reference MDM identities; they do not own competing Party/Organization masters.
- Domain transaction data remains owned by its transactional domain.
- Cross-domain access occurs through governed APIs/events, not direct database access.
- Shared engineering patterns are reused only after they are proven in domain implementations.

## Current status

This artifact is the domain-realization control point. It does not claim that all attributes or rules are finalized. Missing definitions must be resolved before declaring Party/Organization implementation-ready.

## Next realization step

Populate the attribute and relationship matrix from the approved business/functional model, reconcile it against the current MDM code/schema/contracts, classify each item as implemented, missing, inconsistent, or intentionally deferred, and then update implementation and acceptance artifacts accordingly.
