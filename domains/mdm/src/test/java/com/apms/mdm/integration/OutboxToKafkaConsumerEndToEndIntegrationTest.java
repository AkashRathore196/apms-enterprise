package com.apms.mdm.integration;

import com.apms.mdm.common.event.EventEnvelope;
import com.apms.mdm.common.outbox.OutboxEvent;
import com.apms.mdm.common.outbox.OutboxPublisher;
import com.apms.mdm.common.outbox.OutboxRepository;
import com.apms.mdm.common.outbox.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@EmbeddedKafka(
        partitions = 1,
        topics = "mdm.party.approved.v1",
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@SpringBootTest(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.listener.missing-topics-fatal=false",
        "mdm.outbox.poll-ms=600000"
})
@ActiveProfiles("test")
class OutboxToKafkaConsumerEndToEndIntegrationTest {

    private static final String TOPIC = "mdm.party.approved.v1";
    private static final String GROUP = "apms-mdm-golden-path";

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> "http://localhost:18080/realms/apms");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> "http://localhost:18080/realms/apms/protocol/openid-connect/certs");
        registry.add("spring.security.oauth2.resourceserver.jwt.jws-algorithms", () -> "RS256");
    }

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private ProcessedEventRepository processedEvents;

    @Autowired
    private OutboxPublisher publisher;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void outboxEventTravelsThroughKafkaAndIsDurablyClaimedOnce() throws Exception {
        UUID partyId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        EventEnvelope envelope = new EventEnvelope(
                eventId,
                TOPIC,
                1,
                "PARTY",
                partyId,
                Instant.now(),
                "mdm-service",
                UUID.randomUUID().toString(),
                null,
                java.util.Map.of("partyId", partyId.toString(), "status", "APPROVED"));
        String payload = objectMapper.writeValueAsString(envelope);

        OutboxEvent outbox = outboxRepository.save(
                new OutboxEvent("PARTY", partyId, TOPIC, 1, payload));

        publisher.publish();

        waitUntil(() -> {
            OutboxEvent current = outboxRepository.findById(outbox.getEventId()).orElse(null);
            return current != null && "PUBLISHED".equals(current.getStatus());
        });

        waitUntil(() -> processedEvents.countByEventIdAndConsumerGroup(eventId.toString(), GROUP) == 1);

        assertNotNull(outboxRepository.findById(outbox.getEventId()).orElse(null));
        assertEquals("PUBLISHED", outboxRepository.findById(outbox.getEventId()).orElseThrow().getStatus());
        assertEquals(1, processedEvents.countByEventIdAndConsumerGroup(eventId.toString(), GROUP));

        kafkaTemplate.send(TOPIC, partyId.toString(), payload).get();
        Thread.sleep(1000);

        assertEquals(1, processedEvents.countByEventIdAndConsumerGroup(eventId.toString(), GROUP));
    }

    private static void waitUntil(Check check) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 15000;
        while (System.currentTimeMillis() < deadline) {
            if (check.ok()) {
                return;
            }
            Thread.sleep(250);
        }
        throw new AssertionError("Condition was not satisfied within 15 seconds");
    }

    @FunctionalInterface
    private interface Check {
        boolean ok();
    }
}
