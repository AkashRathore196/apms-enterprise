package com.apms.mdm.integration;

import com.apms.mdm.common.outbox.OutboxEvent;
import com.apms.mdm.common.outbox.OutboxPublisher;
import com.apms.mdm.common.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutboxPublisherIdempotencyUnitTest {

    @Test
    void pendingEventIsPublishedAndMarkedPublishedAfterKafkaAck() {
        UUID aggregateId = UUID.randomUUID();
        OutboxEvent event = new OutboxEvent(
                "PARTY",
                aggregateId,
                "mdm.party.approved.v1",
                1,
                "{\"partyId\":\"" + aggregateId + "\"}"
        );

        RecordingRepository repository = new RecordingRepository(event);
        RecordingKafkaTemplate kafka = new RecordingKafkaTemplate();
        new OutboxPublisher(repository, kafka).publish();

        assertEquals(1, kafka.sendCount);
        assertEquals("mdm.party.approved.v1", kafka.lastTopic);
        assertEquals("PUBLISHED", repository.findById(event.getEventId()).orElseThrow().getStatus());
    }

    private static final class RecordingRepository implements OutboxRepository {
        private final OutboxEvent event;

        private RecordingRepository(OutboxEvent event) {
            this.event = event;
        }

        @Override public List<OutboxEvent> findAll() { return List.of(event); }
        @Override public Optional<OutboxEvent> findById(UUID id) {
            return event.getEventId().equals(id) ? Optional.of(event) : Optional.empty();
        }
        @Override public <S extends OutboxEvent> S save(S entity) { return entity; }
    }

    private static final class RecordingKafkaTemplate extends KafkaTemplate<String, String> {
        private int sendCount;
        private String lastTopic;

        private RecordingKafkaTemplate() { super(null); }

        @Override
        public CompletableFuture<SendResult<String, String>> send(String topic, String key, String data) {
            sendCount++;
            lastTopic = topic;
            return CompletableFuture.completedFuture(null);
        }
    }
}
