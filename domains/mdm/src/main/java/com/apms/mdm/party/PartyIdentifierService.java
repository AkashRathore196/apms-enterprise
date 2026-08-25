package com.apms.mdm.party;

import com.apms.mdm.common.audit.AuditEntry;
import com.apms.mdm.common.audit.AuditRepository;
import com.apms.mdm.common.outbox.OutboxEvent;
import com.apms.mdm.common.outbox.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PartyIdentifierService {
    private static final List<PartyIdentifierLifecycleState> CURRENT_STATES =
            List.of(PartyIdentifierLifecycleState.ACTIVE, PartyIdentifierLifecycleState.SUSPENDED);

    private final PartyRepository parties;
    private final PartyIdentifierRepository identifiers;
    private final AuditRepository auditRepository;
    private final OutboxRepository outboxRepository;

    public PartyIdentifierService(PartyRepository parties,
                                  PartyIdentifierRepository identifiers,
                                  AuditRepository auditRepository,
                                  OutboxRepository outboxRepository) {
        this.parties = parties;
        this.identifiers = identifiers;
        this.auditRepository = auditRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public UUID create(UUID partyId, String identifierType, String identifierValue,
                       String issuingAuthority, String issuingJurisdiction, String sourceSystem,
                       Instant validFrom, Instant validTo, boolean isPrimary) {
        Party party = parties.findById(partyId).orElseThrow();
        String normalized = normalize(identifierValue);
        if (identifiers.existsByIdentifierTypeAndNormalizedIdentifierValueAndIssuingAuthorityAndIssuingJurisdictionAndLifecycleStateIn(
                identifierType, normalized, blankToNull(issuingAuthority), blankToNull(issuingJurisdiction), CURRENT_STATES)) {
            throw new IllegalStateException("MDM-006 DUPLICATE_IDENTIFIER");
        }
        if (isPrimary && identifiers.countByPartyPartyIdAndIdentifierTypeAndPrimaryTrueAndLifecycleState(
                partyId, identifierType, PartyIdentifierLifecycleState.ACTIVE) > 0) {
            throw new IllegalStateException("MDM-007 DUPLICATE_PRIMARY_IDENTIFIER");
        }

        PartyIdentifier identifier = new PartyIdentifier(
                party, identifierType, identifierValue, normalized,
                issuingAuthority, issuingJurisdiction, sourceSystem,
                validFrom, validTo, isPrimary);
        identifiers.save(identifier);
        audit(identifier, "CREATED");
        event(identifier, "mdm.party.identifier.created.v1");
        return identifier.getIdentifierId();
    }

    @Transactional(readOnly = true)
    public List<PartyIdentifier> list(UUID partyId) {
        if (!parties.existsById(partyId)) {
            throw new IllegalStateException("MDM-002 PARTY_NOT_FOUND");
        }
        return identifiers.findAllByPartyPartyIdOrderByCreatedAtAsc(partyId);
    }

    @Transactional
    public void suspend(UUID partyId, UUID identifierId) {
        PartyIdentifier identifier = getOwned(partyId, identifierId);
        identifier.suspend();
        audit(identifier, "SUSPENDED");
        event(identifier, "mdm.party.identifier.suspended.v1");
    }

    @Transactional
    public void expire(UUID partyId, UUID identifierId) {
        PartyIdentifier identifier = getOwned(partyId, identifierId);
        identifier.expire();
        audit(identifier, "EXPIRED");
        event(identifier, "mdm.party.identifier.expired.v1");
    }

    @Transactional
    public void retire(UUID partyId, UUID identifierId) {
        PartyIdentifier identifier = getOwned(partyId, identifierId);
        identifier.retire();
        audit(identifier, "RETIRED");
        event(identifier, "mdm.party.identifier.retired.v1");
    }

    @Transactional
    public void setPrimary(UUID partyId, UUID identifierId, boolean isPrimary) {
        PartyIdentifier identifier = getOwned(partyId, identifierId);
        if (isPrimary && identifiers.countByPartyPartyIdAndIdentifierTypeAndPrimaryTrueAndLifecycleState(
                partyId, identifier.getIdentifierType(), PartyIdentifierLifecycleState.ACTIVE) > 0
                && !identifier.isPrimary()) {
            throw new IllegalStateException("MDM-007 DUPLICATE_PRIMARY_IDENTIFIER");
        }
        identifier.changePrimary(isPrimary);
        audit(identifier, isPrimary ? "PRIMARY" : "UNPRIMARY");
        event(identifier, "mdm.party.identifier.primary-changed.v1");
    }

    private PartyIdentifier getOwned(UUID partyId, UUID identifierId) {
        return identifiers.findByIdentifierIdAndPartyPartyId(identifierId, partyId).orElseThrow();
    }

    private void audit(PartyIdentifier identifier, String operation) {
        auditRepository.save(new AuditEntry("PARTY_IDENTIFIER", identifier.getIdentifierId(), operation, "system"));
    }

    private void event(PartyIdentifier identifier, String eventType) {
        UUID partyId = identifier.getParty().getPartyId();
        outboxRepository.save(new OutboxEvent(
                "PARTY_IDENTIFIER", identifier.getIdentifierId(), eventType, 1,
                "{\"identifierId\":\"" + identifier.getIdentifierId() + "\",\"partyId\":\"" + partyId + "\"}"
        ));
    }

    static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
