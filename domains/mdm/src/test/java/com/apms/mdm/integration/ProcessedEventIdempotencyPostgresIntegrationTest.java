package com.apms.mdm.integration;

import com.apms.mdm.common.outbox.ProcessedEvent;
import com.apms.mdm.common.outbox.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
    void sameEventIsIdempotentPerConsumerGroupButIndependentAcrossGroups() {
        String eventId = UUID.randomUUID().toString();
        String groupA = "apms-mdm-golden-path";
        String groupB = "apms-mdm-projection";

        processedEvents.save(new ProcessedEvent(eventId, groupA));
        processedEvents.save(new ProcessedEvent(eventId, groupB));

        assertEquals(2, processedEvents.count());
        assertEquals(1, processedEvents.countByEventId(eventId));
        assertEquals(1, processedEvents.countByEventIdAndConsumerGroup(eventId, groupA));
        assertEquals(1, processedEvents.countByEventIdAndConsumerGroup(eventId, groupB));
    }
}
