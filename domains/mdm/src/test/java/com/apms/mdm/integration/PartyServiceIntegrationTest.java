package com.apms.mdm.integration;

import com.apms.mdm.common.audit.AuditRepository;
import com.apms.mdm.common.outbox.OutboxRepository;
import com.apms.mdm.party.OrganizationRepository;
import com.apms.mdm.party.PartyRepository;
import com.apms.mdm.party.PartyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class PartyServiceIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired PartyService partyService;
    @Autowired PartyRepository partyRepository;
    @Autowired OrganizationRepository organizationRepository;
    @Autowired AuditRepository auditRepository;
    @Autowired OutboxRepository outboxRepository;

    @Test
    void createSubmitApprovePersistsMasterAuditAndOutbox() {
        UUID partyId = partyService.createOrganization("APMS Integration Test Org", "APMS Test Org");

        assertTrue(partyRepository.findById(partyId).isPresent());
        assertEquals("APMS Integration Test Org",
                organizationRepository.findByPartyPartyId(partyId).getLegalName());

        partyService.submit(partyId);
        partyService.approve(partyId);

        assertEquals("ACTIVE", partyRepository.findById(partyId).orElseThrow()
                .getLifecycleState().name());

        assertTrue(auditRepository.count() >= 3);
        assertTrue(outboxRepository.count() >= 3);
    }
}
