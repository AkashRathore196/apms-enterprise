package com.apms.mdm.integration.kafka;

import com.apms.mdm.common.event.EventEnvelope;
import com.apms.mdm.common.outbox.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PartyEventConsumer {
    static final String GROUP = "apms-mdm-golden-path";

    private final ProcessedEventRepository processedEvents;
    private final ObjectMapper objectMapper;

    public PartyEventConsumer(ProcessedEventRepository processedEvents, ObjectMapper objectMapper) {
        this.processedEvents = processedEvents;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {
            "mdm.party.created.v1",
            "mdm.party.submitted.v1",
            "mdm.party.approved.v1"
    }, groupId = GROUP)
    @Transactional
    public void consume(String rawEvent) throws Exception {
        EventEnvelope event = objectMapper.readValue(rawEvent, EventEnvelope.class);
        String eventId = event.eventId().toString();

        // Atomically claim the event for this consumer group. A duplicate delivery
        // observes zero affected rows and cannot execute the downstream effect.
        if (processedEvents.claimIfUnprocessed(eventId, GROUP) == 0) {
            return;
        }

        // Downstream projection/business handling belongs here.
        // The claim and business handling are in the same database transaction.
    }
}
