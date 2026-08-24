# MDM Party Lifecycle Merge Gate v1.0

## Scope

This gate covers only the Party lifecycle realization in PR #16. Party Identifier and Party Relationship runtime persistence remain separately gated.

## Required implementation evidence

1. Canonical lifecycle is enforced in the domain model:
   `DRAFT -> SUBMITTED -> REVIEW -> APPROVED -> ACTIVE`.
2. Governed operational transitions are enforced:
   `ACTIVE -> SUSPENDED -> ACTIVE` and `ACTIVE|SUSPENDED -> RETIRED`.
3. Legacy generic Party `status` is removed from the persistence model by migration.
4. `created_at` and `updated_at` are mapped by JPA and remain non-null.
5. Lifecycle transition tests cover both valid transitions and invalid skips.
6. Normal MDM build/regression CI remains the acceptance mechanism for this slice.
7. Keycloak/Kong security acceptance is explicitly deferred and is not a merge prerequisite for this domain slice.

## Merge rule

PR #16 may proceed when the above implementation evidence is present and the normal MDM build/regression gates are green. No Identifier/Relationship runtime implementation should be added to this slice.
