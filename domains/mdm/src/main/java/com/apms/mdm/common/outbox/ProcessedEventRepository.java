package com.apms.mdm.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, ProcessedEventId> {

    @Modifying
    @Query(value = "INSERT INTO processed_event (event_id, consumer_group, processed_at) "
            + "VALUES (:eventId, :consumerGroup, CURRENT_TIMESTAMP) "
            + "ON CONFLICT (event_id, consumer_group) DO NOTHING", nativeQuery = true)
    int claimIfUnprocessed(@Param("eventId") String eventId,
                           @Param("consumerGroup") String consumerGroup);

    long countByEventId(String eventId);
    long countByEventIdAndConsumerGroup(String eventId, String consumerGroup);
}
