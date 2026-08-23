package com.apms.mdm.common.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@IdClass(ProcessedEventId.class)
@Table(name = "processed_event")
public class ProcessedEvent {
    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private String eventId;

    @Id
    @Column(name = "consumer_group", nullable = false, updatable = false)
    private String consumerGroup;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt = Instant.now();

    protected ProcessedEvent() {}

    public ProcessedEvent(String eventId, String consumerGroup) {
        this.eventId = eventId;
        this.consumerGroup = consumerGroup;
    }
}
