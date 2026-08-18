package com.apms.mdm.common.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_entry")
public class AuditEntry {
    @Id
    @Column(name = "audit_id", nullable = false, updatable = false)
    private UUID auditId = UUID.randomUUID();

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private String actor;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt = Instant.now();

    protected AuditEntry() {}

    public AuditEntry(String entityType, UUID entityId, String operation, String actor) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.operation = operation;
        this.actor = actor;
    }
}
