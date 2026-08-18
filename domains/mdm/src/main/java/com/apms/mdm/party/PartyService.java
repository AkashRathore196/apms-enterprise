package com.apms.mdm.party;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PartyService {
    private final PartyRepository parties;
    private final OrganizationRepository organizations;

    public PartyService(PartyRepository parties, OrganizationRepository organizations) {
        this.parties = parties;
        this.organizations = organizations;
    }

    @Transactional
    public UUID createOrganization(String legalName, String displayName) {
        if (organizations.existsByLegalNameIgnoreCase(legalName)) {
            throw new IllegalStateException("MDM-002 DUPLICATE_MASTER");
        }
        Party party = parties.save(new Party(PartyType.ORGANIZATION));
        organizations.save(new Organization(party, legalName, displayName));
        return party.getPartyId();
    }

    @Transactional
    public void submit(UUID partyId) {
        Party party = parties.findById(partyId).orElseThrow();
        party.submit();
    }

    @Transactional
    public void approve(UUID partyId) {
        Party party = parties.findById(partyId).orElseThrow();
        party.approve();
    }
}
