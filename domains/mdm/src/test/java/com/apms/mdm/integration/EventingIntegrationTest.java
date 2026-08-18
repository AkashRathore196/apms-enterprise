package com.apms.mdm.integration;

import com.apms.mdm.common.event.EventEnvelope;
import com.apms.mdm.common.outbox.ProcessedEvent;
import com.apms.mdm.integration.kafka.PartyEventConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventingIntegrationTest {
    @Test
    void duplicateEventIsProcessedOnlyOnce() throws Exception {
        TestProcessedEventStore store = new TestProcessedEventStore();
        PartyEventConsumer consumer = new PartyEventConsumer(store, new ObjectMapper());

        UUID eventId = UUID.randomUUID();
        EventEnvelope event = new EventEnvelope(
                eventId,
                "mdm.party.approved.v1",
                1,
                "PARTY",
                UUID.randomUUID(),
                Instant.now(),
                "apms-mdm",
                "corr-1",
                null,
                java.util.Map.of("partyId", "test")
        );
        String raw = new ObjectMapper().writeValueAsString(event);

        consumer.consume(raw);
        consumer.consume(raw);

        assertEquals(1, store.count());
        assertTrue(store.exists(eventId.toString()));
    }

    private static final class TestProcessedEventStore
            implements PartyEventConsumer.ProcessedEventStore {
        private final Set<String> eventIds = new HashSet<>();

        @Override
        public boolean exists(String eventId) {
            return eventIds.contains(eventId);
        }

        @Override
        public void save(ProcessedEvent event) {
            try {
                var field = ProcessedEvent.class.getDeclaredField("eventId");
                field.setAccessible(true);
                eventIds.add((String) field.get(event));
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }

        int count() {
            return eventIds.size();
        }
    }
}
