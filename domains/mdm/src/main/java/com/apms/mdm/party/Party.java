package com.apms.mdm.party;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Version
    @Column(nullable = false)
    private long version;

    protected Party() {}

    public Party(PartyType partyType) { this.partyType = partyType; }

    public UUID getPartyId() { return partyId; }
    public PartyType getPartyType() { return partyType; }
    public LifecycleState getLifecycleState() { return lifecycleState; }
    public long getVersion() { return version; }

    public void submit() {
        if (lifecycleState != LifecycleState.DRAFT) {
            throw new IllegalStateException("MDM-001 INVALID_STATE");
        }
        lifecycleState = LifecycleState.SUBMITTED;
    }

    public void approve() {
        if (lifecycleState != LifecycleState.SUBMITTED && lifecycleState != LifecycleState.REVIEW) {
            throw new IllegalStateException("MDM-001 INVALID_STATE");
        }
        lifecycleState = LifecycleState.ACTIVE;
    }
}
