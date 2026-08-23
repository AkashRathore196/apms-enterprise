package com.apms.mdm.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, ProcessedEventId> {
    boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup);
}
