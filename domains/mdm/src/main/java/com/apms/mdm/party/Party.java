package com.apms.mdm.party;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "party")
public class Party {
    @Id
    @Column(name = "party_id", nullable = false, updatable = false)
    private UUID partyId = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", nullable = false)
    private PartyType partyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_state", nullable = false)
    private LifecycleState lifecycleState = LifecycleState.DRAFT;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Party() {}

    public Party(PartyType partyType) {
        this.partyType = partyType;
    }

    public UUID getPartyId() { return partyId; }
    public PartyType getPartyType() { return partyType; }
    public LifecycleState getLifecycleState() { return lifecycleState; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void submit() {
        require(LifecycleState.DRAFT);
        lifecycleState = LifecycleState.SUBMITTED;
        touch();
    }

    public void moveToReview() {
        require(LifecycleState.SUBMITTED);
        lifecycleState = LifecycleState.REVIEW;
        touch();
    }

    public void approve() {
        if (lifecycleState != LifecycleState.REVIEW) {
            throw invalidState();
        }
        lifecycleState = LifecycleState.APPROVED;
        touch();
    }

    public void activate() {
        if (lifecycleState != LifecycleState.APPROVED) {
            throw invalidState();
        }
        lifecycleState = LifecycleState.ACTIVE;
        touch();
    }

    public void suspend() {
        require(LifecycleState.ACTIVE);
        lifecycleState = LifecycleState.SUSPENDED;
        touch();
    }

    public void reactivate() {
        require(LifecycleState.SUSPENDED);
        lifecycleState = LifecycleState.ACTIVE;
        touch();
    }

    public void retire() {
        require(LifecycleState.ACTIVE, LifecycleState.SUSPENDED);
        lifecycleState = LifecycleState.RETIRED;
        touch();
    }

    private void require(LifecycleState... allowed) {
        for (LifecycleState state : allowed) {
            if (lifecycleState == state) {
                return;
            }
        }
        throw invalidState();
    }

    private IllegalStateException invalidState() {
        return new IllegalStateException("MDM-001 INVALID_STATE");
    }

    private void touch() {
        updatedAt = Instant.now();
    }
}
