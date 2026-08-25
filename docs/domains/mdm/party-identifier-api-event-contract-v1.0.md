# MDM Party Identifier API & Event Contract v1.0

## Scope

This contract derives directly from the approved Party Identifier semantics in Issue #17. It defines the bounded REST and event surface without introducing identifier taxonomy values or search semantics not yet approved.

## REST resource

Resource: `/parties/{partyId}/identifiers`

### Create

`POST /parties/{partyId}/identifiers`

Required request fields:
- `identifierType`
- `identifierValue`

Optional request fields:
- `issuingAuthority`
- `issuingJurisdiction`
- `sourceSystem`
- `validFrom`
- `validTo`
- `isPrimary`

The service derives `normalizedIdentifierValue` using the approved identifier-type normalization contract. Clients do not submit the canonical normalized value as an authoritative field.

### Read

`GET /parties/{partyId}/identifiers`

Returns identifiers associated with the Party. The initial contract does not introduce arbitrary filtering or search parameters beyond Party ownership.

### Lifecycle operations

- `POST /parties/{partyId}/identifiers/{identifierId}/suspend`
- `POST /parties/{partyId}/identifiers/{identifierId}/expire`
- `POST /parties/{partyId}/identifiers/{identifierId}/retire`
- `POST /parties/{partyId}/identifiers/{identifierId}/primary`

Primary operation request:
- `isPrimary` (boolean)

### Response

The response representation includes:
- `identifierId`
- `partyId`
- `identifierType`
- `identifierValue`
- `issuingAuthority`
- `issuingJurisdiction`
- `sourceSystem`
- `validFrom`
- `validTo`
- `isPrimary`
- `lifecycleState`
- `version`
- `createdAt`
- `updatedAt`

`normalizedIdentifierValue` is an internal persistence/matching value and is not exposed as a general-purpose client field.

## Validation/error contract

- Missing required identifier field -> `400`
- Invalid validity window -> `400`
- Duplicate current governed identity -> `409`
- Invalid identifier lifecycle transition -> `409`
- Primary designation conflict -> `409`
- Party or identifier not found -> `404`

## Events

Event names:
- `mdm.party.identifier.created.v1`
- `mdm.party.identifier.suspended.v1`
- `mdm.party.identifier.expired.v1`
- `mdm.party.identifier.retired.v1`
- `mdm.party.identifier.primary-changed.v1`

Events use the existing versioned event-envelope pattern and contain the identifier record identity plus governed state relevant to the event. The implementation must retain stable `eventId`, aggregate/party identity, event type, version, timestamp, source, correlation metadata, and payload semantics.

## Audit

Every lifecycle-changing operation must create an audit record and a corresponding outbox event in the same transaction boundary.

## Explicit exclusions

This v1.0 contract does not define:
- additional identifier taxonomy values
- identifier-specific checksum rules not yet governed
- arbitrary identifier search/index APIs
- multiple primary contexts
- Party Relationship runtime behavior
- Keycloak/Kong security acceptance behavior
