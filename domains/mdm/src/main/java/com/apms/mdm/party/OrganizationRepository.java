package com.apms.mdm.party;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    boolean existsByLegalNameIgnoreCase(String legalName);
    Organization findByPartyPartyId(UUID partyId);
}
