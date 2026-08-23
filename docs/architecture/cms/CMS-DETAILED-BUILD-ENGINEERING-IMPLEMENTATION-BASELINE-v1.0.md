# APMS CMS Detailed Build Engineering & Implementation Baseline v1.0

**Status:** Implementation Baseline — In Progress  
**Capability:** Commercial Management System (CMS)  
**Reference Architecture:** APMS Enterprise Meta-Architecture  
**Engineering Reference:** APMS MDM Detailed Build Engineering & Implementation Baseline v1.0  
**Build Sequence:** Capability → Business → Functional → Information → Data → Application → Integration → Implementation → Acceptance → Freeze

---

## 1. Purpose

This document establishes the detailed engineering and implementation baseline for the APMS Commercial Management System (CMS).

CMS is the next controlled implementation capability following the proven MDM Party/Organization Golden Path.

The baseline converts the approved enterprise architecture and reconciled CMS capability boundary into an implementation-ready engineering contract.

## 2. Architectural Position

CMS owns the commercial lifecycle from initial commercial engagement through commercial handover.

### CMS-owned lifecycle

Lead → Enquiry → Qualification → Requirement → Site Survey → Solution Design → Estimate → Proposal → Quotation → Negotiation → Contract → Commercial Approval → Commercial Handover

Commercial Handover is the formal CMS boundary into project execution.

## 3. Domain Ownership

CMS owns:

- commercial enquiries and opportunities
- customer commercial requirements
- commercial qualification
- requirements capture
- solution/proposal preparation
- estimates
- quotations
- negotiations
- commercial contracts
- commercial approvals
- commercial lifecycle state
- commercial audit history
- CMS domain events
- commercial handover

CMS does not own:

- Party/Organization master data — MDM
- project execution — PPMS
- accounting/financial transactions — FMS
- enterprise workflow execution — EWOP
- enterprise identity — EIAP
- enterprise search — ESP
- enterprise event infrastructure — EIP
- quality management — QMS

## 4. Engineering Principles

1. CMS is the authoritative owner of CMS transactional data.
2. CMS uses a database-per-domain boundary.
3. CMS does not directly access another domain's database.
4. APIs are the synchronous integration contract.
5. Kafka events are the asynchronous integration contract.
6. Business transaction, audit and outbox state are committed atomically.
7. Durable consumers require idempotency protection.
8. Enterprise IAM is reused; CMS does not create local identity management.
9. Enterprise workflow is delegated to EWOP.
10. Enterprise search is implemented through the approved search platform.
11. Observability follows the enterprise OpenTelemetry baseline.
12. CI separates deterministic build validation from runtime acceptance.
13. Production implementation must reconcile against this baseline before freeze.

## 5. Golden Path

API Request
→ Authentication
→ Authorization
→ Validation
→ Domain Rules
→ Transaction
→ CMS State
→ Audit
→ Outbox
→ Commit
→ Publisher
→ Kafka
→ Consumer
→ Durable Idempotency
→ Business Outcome
→ Observability

## 6. Implementation Gates

- Capability reconciliation
- Business validation
- Functional validation
- Information validation
- Data validation
- Application validation
- Integration validation
- Security validation
- Implementation validation
- CI validation
- PostgreSQL acceptance
- Kafka acceptance
- Eventing E2E acceptance
- Idempotency acceptance
- Regression acceptance
- Architecture freeze

## 7. Freeze Criterion

CMS shall not be declared implementation-frozen until all mandatory engineering and acceptance gates are green and the implementation has been reconciled against this baseline.
