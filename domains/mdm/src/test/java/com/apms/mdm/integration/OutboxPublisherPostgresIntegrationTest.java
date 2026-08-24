package com.apms.mdm.integration;

import com.apms.mdm.common.outbox.OutboxEvent;
import com.apms.mdm.common.outbox.OutboxPublisher;
import com.apms.mdm.common.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class OutboxPublisherPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.listener.auto-startup", () -> "false");
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> "http://localhost:18080/realms/apms");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> "http://localhost:18080/realms/apms/protocol/openid-connect/certs");
        registry.add("spring.security.oauth2.resourceserver.jwt.jws-algorithms", () -> "RS256");
    }

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private OutboxPublisher publisher;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void publishesPendingOutboxEventAndMarksItPublishedAfterKafkaAck() throws Exception {
        UUID partyId = UUID.randomUUID();
        OutboxEvent event = outboxRepository.save(
                new OutboxEvent("PARTY", partyId, "mdm.party.approved.v1", 1,
                        "{\"partyId\":\"" + partyId + "\"}"));

        CompletableFuture<org.springframework.kafka.support.SendResult<String, String>> ack =
                CompletableFuture.completedFuture(null);
        org.mockito.Mockito.when(kafkaTemplate.send(
                eq("mdm.party.approved.v1"), eq(partyId.toString()), eq(event.getPayload())))
                .thenReturn(ack);

        publisher.publish();

        verify(kafkaTemplate, timeout(2000)).send(
                eq("mdm.party.approved.v1"), eq(partyId.toString()), eq(event.getPayload()));

        OutboxEvent reloaded = outboxRepository.findById(event.getEventId()).orElseThrow();
        assertEquals("PUBLISHED", reloaded.getStatus());
    }
}
