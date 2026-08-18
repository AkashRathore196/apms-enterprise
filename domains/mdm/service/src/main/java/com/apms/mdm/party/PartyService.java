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
    public PartyController.PartyResponse createOrganization(PartyController.CreateOrganizationRequest request) {
        if (organizations.existsByLegalNameIgnoreCase(request.legalName())) {
            throw new IllegalStateException("MDM-002 DUPLICATE_MASTER");
        }

        Party party = parties.save(new Party(PartyType.ORGANIZATION));
        Organization organization = organizations.save(
                new Organization(party, request.legalName(), request.displayName()));
        return response(party, organization);
    }

    @Transactional
    public PartyController.PartyResponse submit(UUID partyId) {
        Party party = parties.findById(partyId).orElseThrow();
        party.submit();
        return response(party, organizations.findByPartyPartyId(partyId));
    }

    @Transactional
    public PartyController.PartyResponse approve(UUID partyId) {
        Party party = parties.findById(partyId).orElseThrow();
        party.approve();
        return response(party, organizations.findByPartyPartyId(partyId));
    }

    private PartyController.PartyResponse response(Party party, Organization organization) {
        return new PartyController.PartyResponse(
                party.getPartyId(),
                organization.getOrganizationId(),
                party.getLifecycleState().name(),
                party.getVersion());
    }
}
