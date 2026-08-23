package com.apms.mdm.integration;

import com.apms.mdm.common.outbox.ProcessedEvent;
import com.apms.mdm.common.outbox.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class ProcessedEventIdempotencyPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProcessedEventRepository processedEvents;

    @Test
    @Transactional
    void duplicateClaimIsIdempotentForOneConsumerGroup() {
        String eventId = UUID.randomUUID().toString();
        String consumerGroup = "apms-mdm-golden-path";

        int firstClaim = processedEvents.claimIfUnprocessed(eventId, consumerGroup);
        int duplicateClaim = processedEvents.claimIfUnprocessed(eventId, consumerGroup);
        processedEvents.flush();

        assertEquals(1, firstClaim);
        assertEquals(0, duplicateClaim);
        assertEquals(1, processedEvents.countByEventIdAndConsumerGroup(eventId, consumerGroup));
    }

    @Test
    @Transactional
    void sameEventIsIndependentlyClaimableByDifferentConsumerGroups() {
        String eventId = UUID.randomUUID().toString();
        String groupA = "apms-mdm-golden-path";
        String groupB = "apms-mdm-projection";

        int firstGroupClaim = processedEvents.claimIfUnprocessed(eventId, groupA);
        int secondGroupClaim = processedEvents.claimIfUnprocessed(eventId, groupB);
        processedEvents.flush();

        assertEquals(1, firstGroupClaim);
        assertEquals(1, secondGroupClaim);
        assertEquals(2, processedEvents.countByEventId(eventId));
        assertEquals(1, processedEvents.countByEventIdAndConsumerGroup(eventId, groupA));
        assertEquals(1, processedEvents.countByEventIdAndConsumerGroup(eventId, groupB));
    }

    @Test
    void compositeJpaIdentitySupportsIndependentConsumerGroups() {
        String eventId = UUID.randomUUID().toString();
        String groupA = "apms-mdm-golden-path";
        String groupB = "apms-mdm-projection";

        processedEvents.save(new ProcessedEvent(eventId, groupA));
        processedEvents.save(new ProcessedEvent(eventId, groupB));
        processedEvents.flush();

        assertEquals(2, processedEvents.countByEventId(eventId));
    }
}
