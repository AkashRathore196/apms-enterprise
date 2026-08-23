# MDM Party/Organization Lifecycle Matrix v1.0

## Purpose

Establish the business-state machine that must be reconciled before further schema expansion or runtime implementation.

## Current implementation states

`DRAFT → SUBMITTED → REVIEW → ACTIVE`

Current code also declares `APPROVED`, `SUSPENDED`, and `RETIRED`, but the implementation does not yet define the complete transition matrix for all states.

## Target decision matrix

| State | Entry condition | Allowed next states | Business owner | Key controls |
|---|---|---|---|---|
| DRAFT | Record created | SUBMITTED | MDM data steward / authorized creator | Minimum required data |
| SUBMITTED | Submission request accepted | REVIEW / APPROVED* | MDM governance | Validation + duplicate check |
| REVIEW | Manual/controlled review required | APPROVED / REJECTED* / DRAFT* | MDM approver | Evidence + authorization |
| APPROVED | Approval completed | ACTIVE | MDM authority | Approval evidence |
| ACTIVE | Authoritative master record | SUSPENDED / RETIRED | MDM authority | Valid change controls |
| SUSPENDED | Temporarily invalid/unavailable | ACTIVE / RETIRED | MDM authority | Reason + effective dates |
| RETIRED | Master record no longer valid | — or controlled reactivation if business-approved | MDM authority | Historical preservation |

`*` Transitions marked with an asterisk require explicit business confirmation; they are not assumed implementation rules.

## Status vs lifecycle

The current `status` string and `lifecycleState` enum appear to overlap. Before additional implementation, decide whether:

1. lifecycle state alone represents business state, or
2. operational status is a separate orthogonal concept.

Do not add additional status values until this decision is approved.

## Required lifecycle semantics

Resolve before implementation-ready freeze:

- transition authorization
- transition validations
- approval semantics
- rejection semantics
- suspension reasons
- retirement semantics
- effective dating
- temporal history
- merge behavior
- duplicate/survivorship interaction
- audit events per transition
- API command semantics
- domain events emitted for material transitions
