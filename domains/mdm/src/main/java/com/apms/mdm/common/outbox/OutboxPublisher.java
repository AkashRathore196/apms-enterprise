package com.apms.mdm.common.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafka;

    public OutboxPublisher(OutboxRepository repository, KafkaTemplate<String, String> kafka) {
        this.repository = repository;
        this.kafka = kafka;
    }

    @Scheduled(fixedDelayString = "${mdm.outbox.poll-ms:1000}")
    public void publish() {
        for (OutboxEvent event : repository.findAll().stream()
                .filter(e -> "PENDING".equals(e.getStatus()))
                .limit(100)
                .toList()) {
            kafka.send(event.getEventType(), event.getAggregateId().toString(), event.getPayload())
                    .whenComplete((result, error) -> {
                        if (error == null) {
                            markPublished(event.getEventId());
                        }
                    });
        }
    }

    @Transactional
    protected void markPublished(java.util.UUID eventId) {
        repository.findById(eventId).ifPresent(event -> {
            event.markPublished();
            repository.save(event);
        });
    }
}
