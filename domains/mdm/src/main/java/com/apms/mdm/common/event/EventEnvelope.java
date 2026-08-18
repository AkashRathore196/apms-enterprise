package com.apms.mdm.common.event;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope(
        UUID eventId,
        String eventType,
        int eventVersion,
        String aggregateType,
        UUID aggregateId,
        Instant occurredAt,
        String producer,
        String correlationId,
        String causationId,
        Object payload) {
}
