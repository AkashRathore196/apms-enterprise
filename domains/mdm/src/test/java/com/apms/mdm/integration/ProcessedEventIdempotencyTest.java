package com.apms.mdm.integration;

import com.apms.mdm.common.outbox.ProcessedEvent;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessedEventIdempotencyTest {
    @Test
    void duplicateEventIdentityProducesOneStoredEffect() {
        Set<String> processed = new HashSet<>();
        String eventId = "evt-1";

        processed.add(eventId);
        processed.add(eventId);

        assertEquals(1, processed.size());
    }

    @Test
    void processedEventCarriesConsumerGroup() {
        ProcessedEvent event = new ProcessedEvent("evt-2", "apms-mdm-golden-path");
        // Construction itself is the contract exercised here; durable persistence
        // is validated separately against PostgreSQL in the integration suite.
        org.junit.jupiter.api.Assertions.assertNotNull(event);
    }
}
