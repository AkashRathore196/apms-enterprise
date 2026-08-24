# APMS Commercial Management System (CMS)

CMS is the APMS commercial lifecycle domain.

## Boundary

Lead → Enquiry → Qualification → Requirement → Site Survey → Solution Design → Estimate → Proposal → Quotation → Negotiation → Contract → Commercial Approval → Commercial Handover

## Ownership

CMS owns commercial transactional state, commercial requirements, estimates, proposals, quotations, negotiations, contracts, approvals, audit history, CMS domain events, and commercial handover.

MDM remains authoritative for Party/Organization master data.

## Engineering rules

- Database-per-domain.
- No direct cross-domain database access.
- Synchronous integration through governed APIs.
- Asynchronous integration through governed Kafka events.
- Transaction, audit and outbox state commit atomically.
- Durable consumers use idempotency protection.
- Enterprise IAM, workflow, search and observability capabilities are reused.
- CMS follows the APMS controlled build and acceptance gates.

## Current state

**Baseline-ready / Runtime implementation gated by MDM acceptance.**
