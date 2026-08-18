package com.apms.mdm.integration;

import com.apms.mdm.common.event.EventEnvelope;
import com.apms.mdm.common.outbox.ProcessedEvent;
import com.apms.mdm.common.outbox.ProcessedEventRepository;
import com.apms.mdm.integration.kafka.PartyEventConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventingIntegrationTest {
    @Test
    void duplicateEventIsProcessedOnlyOnce() throws Exception {
        TestProcessedEventRepository repository = new TestProcessedEventRepository();
        PartyEventConsumer consumer = new PartyEventConsumer(repository, new ObjectMapper());

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
                Map.of("partyId", "test")
        );
        String raw = new ObjectMapper().writeValueAsString(event);

        consumer.consume(raw);
        consumer.consume(raw);

        assertEquals(1, repository.count());
        assertTrue(repository.existsById(eventId.toString()));
    }

    private static final class TestProcessedEventRepository implements ProcessedEventRepository {
        private final Map<String, ProcessedEvent> entries = new HashMap<>();

        @Override
        public boolean existsById(String eventId) {
            return entries.containsKey(eventId);
        }

        @Override
        public <S extends ProcessedEvent> S save(S entity) {
            try {
                var field = ProcessedEvent.class.getDeclaredField("eventId");
                field.setAccessible(true);
                entries.put((String) field.get(entity), entity);
                return entity;
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }

        int count() {
            return entries.size();
        }

        @Override public <S extends ProcessedEvent> java.util.List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<ProcessedEvent> findById(String id) { return java.util.Optional.ofNullable(entries.get(id)); }
        @Override public java.util.List<ProcessedEvent> findAll() { return java.util.List.copyOf(entries.values()); }
        @Override public java.util.List<ProcessedEvent> findAllById(Iterable<String> ids) { throw new UnsupportedOperationException(); }
        @Override public long count() { return entries.size(); }
        @Override public void deleteById(String id) { entries.remove(id); }
        @Override public void delete(ProcessedEvent entity) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllById(Iterable<? extends String> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll(Iterable<? extends ProcessedEvent> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll() { entries.clear(); }
    }
}
