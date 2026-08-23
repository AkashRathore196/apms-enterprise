# MDM Party Identifier Decision Framework v1.0

## Purpose

Provide the controlled decision framework required before Party Identifier persistence and contracts are implemented. This artifact intentionally separates semantic decisions from physical implementation.

## Decision principles

1. MDM is authoritative for the Party-to-Identifier association.
2. Identifier uniqueness is contextual, not globally assumed.
3. Identifier semantics are governed by identifier type and issuing authority/jurisdiction where applicable.
4. External provenance is retained when an external source supplies the identifier.
5. Identifier history is auditable; expiry or retirement does not imply deletion.
6. Primary designation is contextual to an approved identifier scope.
7. Temporal validity is explicit where identifier validity is business-significant.

## Decision matrix

| Decision | Required outcome | Current state |
|---|---|---|
| Identifier taxonomy | Approved reference-data categories and ownership | OPEN |
| Normalization | Per-type normalization/canonicalization rules | OPEN |
| Uniqueness | Exact constraint dimensions by identifier type | OPEN |
| Issuing authority | Authority and jurisdiction semantics | OPEN |
| Lifecycle | Identifier lifecycle and transition rules | OPEN |
| Primary scope | Definition of primary identifier context | OPEN |
| Provenance | Source-system requirements and lineage | OPEN |
| Temporal policy | validFrom/validTo semantics | OPEN |

## Safe implementation boundary

Until the decisions above are approved, do not add production persistence constraints that depend on a specific identifier taxonomy or uniqueness rule.

It is safe to prepare:

- logical identifier contract
- traceability mapping
- validation/test templates
- migration design options
- API/event schema placeholders that do not encode unapproved taxonomy values

It is not yet safe to freeze:

- identifier type enum/reference values
- final unique indexes
- type-specific normalization algorithms
- primary-identifier uniqueness constraints

## Approval output

Issue #17 is complete when each OPEN decision has an approved value, owner, rationale, and implementation consequence sufficient to derive persistence, API/event contracts, validation and executable tests.
