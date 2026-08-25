package com.apms.mdm.party;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PartyIdentifierRepository extends JpaRepository<PartyIdentifier, UUID> {
    boolean existsByIdentifierTypeAndNormalizedIdentifierValueAndIssuingAuthorityAndIssuingJurisdictionAndLifecycleStateIn(
            String identifierType,
            String normalizedIdentifierValue,
            String issuingAuthority,
            String issuingJurisdiction,
            Collection<PartyIdentifierLifecycleState> lifecycleStates);

    long countByPartyPartyIdAndIdentifierTypeAndPrimaryTrueAndLifecycleState(
            UUID partyId,
            String identifierType,
            PartyIdentifierLifecycleState lifecycleState);

    List<PartyIdentifier> findAllByPartyPartyIdOrderByCreatedAtAsc(UUID partyId);
}
