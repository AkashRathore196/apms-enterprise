package com.apms.mdm.integration;

import com.apms.mdm.common.event.EventEnvelope;
import com.apms.mdm.common.outbox.ProcessedEvent;
import com.apms.mdm.common.outbox.ProcessedEventRepository;
import com.apms.mdm.integration.kafka.PartyEventConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
                java.util.Map.of("partyId", "test")
        );
        String raw = new ObjectMapper().writeValueAsString(event);

        consumer.consume(raw);
        consumer.consume(raw);

        assertEquals(1, repository.count());
        assertTrue(repository.existsById(eventId.toString()));
    }

    private static final class TestProcessedEventRepository implements ProcessedEventRepository {
        private final java.util.Map<String, ProcessedEvent> entries = new java.util.HashMap<>();

        @Override public boolean existsById(String id) { return entries.containsKey(id); }
        @Override public <S extends ProcessedEvent> S save(S entity) {
            try {
                var f = ProcessedEvent.class.getDeclaredField("eventId");
                f.setAccessible(true);
                entries.put((String) f.get(entity), entity);
            } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
            return entity;
        }
        @Override public long count() { return entries.size(); }

        @Override public <S extends ProcessedEvent> java.util.List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<ProcessedEvent> findById(String s) { return java.util.Optional.ofNullable(entries.get(s)); }
        @Override public boolean existsById(java.lang.Object id) { return existsById(String.valueOf(id)); }
        @Override public java.util.List<ProcessedEvent> findAll() { return java.util.List.copyOf(entries.values()); }
        @Override public java.util.List<ProcessedEvent> findAllById(Iterable<String> strings) { throw new UnsupportedOperationException(); }
        @Override public void deleteById(String s) { entries.remove(s); }
        @Override public void delete(ProcessedEvent entity) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllById(Iterable<? extends String> strings) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll(Iterable<? extends ProcessedEvent> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll() { entries.clear(); }
        @Override public <S2 extends ProcessedEvent> java.util.List<S2> findAll(org.springframework.data.domain.Sort sort) { throw new UnsupportedOperationException(); }
        @Override public org.springframework.data.domain.Page<ProcessedEvent> findAll(org.springframework.data.domain.Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public <S2 extends ProcessedEvent> java.util.Optional<S2> findById(java.lang.String id, org.springframework.data.domain.Example<S2> example) { throw new UnsupportedOperationException(); }
    }
}
