package com.apms.mdm.party;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PartyIdentifierRepository extends JpaRepository<PartyIdentifier, UUID> {
    boolean existsByIdentifierTypeAndNormalizedIdentifierValueAndIssuingAuthorityAndIssuingJurisdictionAndLifecycleStateIn(
            String identifierType,
            String normalizedIdentifierValue,
            String issuingAuthority,
            String issuingJurisdiction,
            java.util.Collection<PartyIdentifierLifecycleState> lifecycleStates);

    long countByPartyPartyIdAndIdentifierTypeAndPrimaryTrueAndLifecycleState(
            UUID partyId,
            String identifierType,
            PartyIdentifierLifecycleState lifecycleState);
}
