package com.apms.mdm.integration;

import com.apms.mdm.common.event.EventEnvelope;
import com.apms.mdm.common.outbox.ProcessedEvent;
import com.apms.mdm.common.outbox.ProcessedEventRepository;
import com.apms.mdm.integration.kafka.PartyEventConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
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
    void duplicateDeliveryProducesOneDurableProcessingRecord() throws Exception {
        UUID eventId = UUID.randomUUID();
        EventEnvelope envelope = new EventEnvelope(
                eventId,
                "mdm.party.approved.v1",
                1,
                "PARTY",
                UUID.randomUUID(),
                Instant.now(),
                "mdm-service",
                UUID.randomUUID().toString(),
                null,
                java.util.Map.of("partyId", "test-party"));
        String rawEvent = new ObjectMapper().writeValueAsString(envelope);
        PartyEventConsumer consumer = new PartyEventConsumer(processedEvents, new ObjectMapper());

        consumer.consume(rawEvent);
        consumer.consume(rawEvent);

        assertEquals(1, processedEvents.countByEventIdAndConsumerGroup(
                eventId.toString(), "apms-mdm-golden-path"));
    }

    @Test
    void sameEventCanBeProcessedIndependentlyByDifferentConsumerGroups() {
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
