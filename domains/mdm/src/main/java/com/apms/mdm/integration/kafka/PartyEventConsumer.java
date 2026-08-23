package com.apms.mdm.integration.kafka;

import com.apms.mdm.common.event.EventEnvelope;
import com.apms.mdm.common.outbox.ProcessedEvent;
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
        if (processedEvents.existsByEventIdAndConsumerGroup(eventId, GROUP)) {
            return;
        }

        // Downstream projection/business handling belongs here.
        // The processed-event insert is in the same transaction as that handling.
        processedEvents.save(new ProcessedEvent(eventId, GROUP));
    }
}
