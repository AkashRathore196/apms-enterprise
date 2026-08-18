package com.apms.mdm.integration;

import com.apms.mdm.MdmApplication;
import com.apms.mdm.party.LifecycleState;
import com.apms.mdm.party.OrganizationRepository;
import com.apms.mdm.party.PartyRepository;
import com.apms.mdm.party.PartyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = MdmApplication.class)
@ActiveProfiles("test")
@Testcontainers
class MdmRegressionTest {

    @Autowired PartyService partyService;
    @Autowired PartyRepository partyRepository;
    @Autowired OrganizationRepository organizationRepository;

    @Test
    void partyOrganizationLifecyclePersistsAndActivates() {
        var id = partyService.createOrganization("Regression Industries Ltd", "Regression Industries");
        assertTrue(partyRepository.existsById(id));
        assertTrue(organizationRepository.findByPartyPartyId(id) != null);

        partyService.submit(id);
        partyService.approve(id);

        var party = partyRepository.findById(id).orElseThrow();
        assertEquals(LifecycleState.ACTIVE, party.getLifecycleState());
    }
}
