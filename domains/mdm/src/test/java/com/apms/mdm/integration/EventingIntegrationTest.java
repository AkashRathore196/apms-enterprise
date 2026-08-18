package com.apms.mdm.integration;

import com.apms.mdm.common.event.EventEnvelope;
import com.apms.mdm.common.outbox.ProcessedEvent;
import com.apms.mdm.common.outbox.ProcessedEventRepository;
import com.apms.mdm.integration.kafka.PartyEventConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        ObjectMapper mapper = new ObjectMapper();
        String raw = mapper.writeValueAsString(event);

        consumer.consume(raw);
        consumer.consume(raw);

        assertEquals(1, repository.size());
        assertTrue(repository.existsById(eventId.toString()));
    }

    private static final class TestProcessedEventRepository implements ProcessedEventRepository {
        private final Map<String, ProcessedEvent> entries = new HashMap<>();

        @Override
        public boolean existsById(String id) {
            return entries.containsKey(id);
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

        int size() {
            return entries.size();
        }

        @Override public <S extends ProcessedEvent> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public Optional<ProcessedEvent> findById(String id) { return Optional.ofNullable(entries.get(id)); }
        @Override public boolean exists(Example<ProcessedEvent> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends ProcessedEvent> Optional<S> findOne(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends ProcessedEvent> List<S> findAll(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends ProcessedEvent> List<S> findAll(Example<S> example, Sort sort) { throw new UnsupportedOperationException(); }
        @Override public <S extends ProcessedEvent> Page<S> findAll(Example<S> example, Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public <S extends ProcessedEvent> long count(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends ProcessedEvent> boolean exists(Example<S> example, boolean unused) { throw new UnsupportedOperationException(); }
        @Override public List<ProcessedEvent> findAll() { return List.copyOf(entries.values()); }
        @Override public List<ProcessedEvent> findAll(Sort sort) { return findAll(); }
        @Override public Page<ProcessedEvent> findAll(Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public List<ProcessedEvent> findAllById(Iterable<String> ids) { throw new UnsupportedOperationException(); }
        @Override public long count() { return entries.size(); }
        @Override public void deleteById(String id) { entries.remove(id); }
        @Override public void delete(ProcessedEvent entity) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllById(Iterable<? extends String> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll(Iterable<? extends ProcessedEvent> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll() { entries.clear(); }
    }
}
