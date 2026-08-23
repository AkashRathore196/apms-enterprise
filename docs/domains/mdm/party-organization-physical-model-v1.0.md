# APMS MDM Party/Organization Physical Model v1.0

## Purpose

Translate the canonical Party/Organization semantics into an implementation-grade physical model without introducing unapproved business semantics.

## Current authoritative core

### party
- `party_id` UUID primary key
- `party_type` controlled value: PERSON | ORGANIZATION
- `lifecycle_state` canonical business lifecycle
- `version` optimistic concurrency
- `created_at`

### organization
- `organization_id` UUID primary key
- `party_id` unique foreign key to `party`
- `legal_name`
- `display_name`

## Controlled additions required before schema implementation

The following are model-required concepts but must not be physically added until their semantics are approved:

- party identifiers
- organization type/classification
- party/organization relationships
- effective-from/effective-to temporal semantics
- provenance/source-system metadata
- merge/survivorship history
- address/contact boundary
- lifecycle transition audit metadata where not already covered by enterprise audit

## Canonical mapping rules

| Concept | Canonical owner | Physical realization direction |
|---|---|---|
| Party identity | MDM | `party.party_id` |
| Party type | MDM | `party.party_type` |
| Business lifecycle | MDM | `party.lifecycle_state` |
| Optimistic concurrency | MDM | `party.version` |
| Organization specialization | MDM | `organization.party_id` |
| Legal name | MDM | `organization.legal_name` |
| Display name | MDM | `organization.display_name` |

## Contract implications

The same canonical names must be used consistently across:
- Java domain model
- REST/OpenAPI schemas
- event envelopes/payloads
- PostgreSQL columns
- validation rules
- acceptance tests

A field must not be introduced into one layer with a different semantic meaning in another layer.

## Current implementation gap

The physical model is currently only a core Party/Organization seed. It is not yet a complete enterprise master-data model. Remaining concepts are intentionally controlled behind the semantic decision gate.

## Next implementation step

Resolve the remaining semantic decisions in Issue #15, then add versioned Flyway migrations and corresponding Java/API/event/test changes as one traceable implementation slice per approved concept.
