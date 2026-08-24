package com.apms.mdm.party;

import com.apms.mdm.common.audit.AuditEntry;
import com.apms.mdm.common.audit.AuditRepository;
import com.apms.mdm.common.outbox.OutboxEvent;
import com.apms.mdm.common.outbox.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PartyService {
    private final PartyRepository parties;
    private final OrganizationRepository organizations;
    private final AuditRepository auditRepository;
    private final OutboxRepository outboxRepository;

    public PartyService(PartyRepository parties, OrganizationRepository organizations,
                        AuditRepository auditRepository, OutboxRepository outboxRepository) {
        this.parties = parties;
        this.organizations = organizations;
        this.auditRepository = auditRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public UUID createOrganization(String legalName, String displayName) {
        if (organizations.existsByLegalNameIgnoreCase(legalName)) {
            throw new IllegalStateException("MDM-002 DUPLICATE_MASTER");
        }
        Party party = parties.save(new Party(PartyType.ORGANIZATION));
        organizations.save(new Organization(party, legalName, displayName));
        recordAudit(party.getPartyId(), "CREATED");
        recordEvent(party.getPartyId(), "mdm.party.created.v1");
        return party.getPartyId();
    }

    @Transactional
    public void submit(UUID partyId) {
        Party party = parties.findById(partyId).orElseThrow();
        party.submit();
        recordAudit(partyId, "SUBMITTED");
        recordEvent(partyId, "mdm.party.submitted.v1");
    }

    @Transactional
    public void moveToReview(UUID partyId) {
        Party party = parties.findById(partyId).orElseThrow();
        party.moveToReview();
        recordAudit(partyId, "REVIEW");
        recordEvent(partyId, "mdm.party.review.v1");
    }

    @Transactional
    public void approve(UUID partyId) {
        Party party = parties.findById(partyId).orElseThrow();
        party.approve();
        recordAudit(partyId, "APPROVED");
        recordEvent(partyId, "mdm.party.approved.v1");
    }

    @Transactional
    public void activate(UUID partyId) {
        Party party = parties.findById(partyId).orElseThrow();
        party.activate();
        recordAudit(partyId, "ACTIVATED");
        recordEvent(partyId, "mdm.party.activated.v1");
    }

    private void recordAudit(UUID partyId, String operation) {
        auditRepository.save(new AuditEntry("PARTY", partyId, operation, "system"));
    }

    private void recordEvent(UUID partyId, String eventType) {
        outboxRepository.save(new OutboxEvent(
                "PARTY", partyId, eventType, 1,
                "{\"partyId\":\"" + partyId + "\"}"
        ));
    }
}
