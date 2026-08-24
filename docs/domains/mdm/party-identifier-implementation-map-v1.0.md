# Party Identifier Implementation Map v1.0

## Build order

1. Domain/value model for governed identifier semantics.
2. PostgreSQL migration and indexes derived from approved uniqueness rules.
3. Repository/service boundary with immutable Party association.
4. Validation rules and lifecycle transitions.
5. Audit/history behavior for create, update, suspend, expire, retire.
6. REST/OpenAPI representation.
7. Relevant event contract and outbox emission.
8. Unit and PostgreSQL integration tests.

## Constraint discipline

Do not encode a universal global uniqueness rule. Do not introduce taxonomy-specific formats, checksum rules, jurisdiction catalog values, or multiple primary contexts without a governed decision. Preserve supplied representation where provenance requires it while using normalized value for matching/uniqueness.

## Dependency rule

Party Identifier depends on the merged Party lifecycle baseline from PR #16. Relationship runtime implementation is independent and remains gated.

## Acceptance

The implementation slice is ready only when the full functional MDM CI path is green. Keycloak/Kong Security Acceptance remains deferred and is not a prerequisite for this bounded domain slice.
