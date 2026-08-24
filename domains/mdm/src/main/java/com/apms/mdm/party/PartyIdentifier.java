package com.apms.mdm.party;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "party_identifier")
public class PartyIdentifier {
    @Id
    @Column(name = "identifier_id", nullable = false, updatable = false)
    private UUID identifierId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false, updatable = false)
    private Party party;

    @Column(name = "identifier_type", nullable = false, length = 64)
    private String identifierType;

    @Column(name = "identifier_value", nullable = false, length = 512)
    private String identifierValue;

    @Column(name = "normalized_identifier_value", nullable = false, length = 512)
    private String normalizedIdentifierValue;

    @Column(name = "issuing_authority", length = 128)
    private String issuingAuthority;

    @Column(name = "issuing_jurisdiction", length = 128)
    private String issuingJurisdiction;

    @Column(name = "source_system", length = 128)
    private String sourceSystem;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_to")
    private Instant validTo;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_state", nullable = false, length = 32)
    private PartyIdentifierLifecycleState lifecycleState = PartyIdentifierLifecycleState.ACTIVE;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected PartyIdentifier() {}

    public PartyIdentifier(Party party, String identifierType, String identifierValue,
                           String normalizedIdentifierValue, String issuingAuthority,
                           String issuingJurisdiction, String sourceSystem,
                           Instant validFrom, Instant validTo, boolean primary) {
        this.party = party;
        this.identifierType = requireText(identifierType, "identifierType");
        this.identifierValue = requireText(identifierValue, "identifierValue");
        this.normalizedIdentifierValue = requireText(normalizedIdentifierValue, "normalizedIdentifierValue");
        this.issuingAuthority = blankToNull(issuingAuthority);
        this.issuingJurisdiction = blankToNull(issuingJurisdiction);
        this.sourceSystem = blankToNull(sourceSystem);
        this.validFrom = validFrom;
        this.validTo = validTo;
        validateTemporalBounds();
        this.primary = primary;
    }

    public UUID getIdentifierId() { return identifierId; }
    public Party getParty() { return party; }
    public String getIdentifierType() { return identifierType; }
    public String getIdentifierValue() { return identifierValue; }
    public String getNormalizedIdentifierValue() { return normalizedIdentifierValue; }
    public String getIssuingAuthority() { return issuingAuthority; }
    public String getIssuingJurisdiction() { return issuingJurisdiction; }
    public String getSourceSystem() { return sourceSystem; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public boolean isPrimary() { return primary; }
    public PartyIdentifierLifecycleState getLifecycleState() { return lifecycleState; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void suspend() {
        requireState(PartyIdentifierLifecycleState.ACTIVE);
        lifecycleState = PartyIdentifierLifecycleState.SUSPENDED;
        touch();
    }

    public void expire() {
        if (lifecycleState != PartyIdentifierLifecycleState.ACTIVE && lifecycleState != PartyIdentifierLifecycleState.SUSPENDED) {
            throw invalidState();
        }
        lifecycleState = PartyIdentifierLifecycleState.EXPIRED;
        primary = false;
        touch();
    }

    public void retire() {
        if (lifecycleState != PartyIdentifierLifecycleState.ACTIVE && lifecycleState != PartyIdentifierLifecycleState.SUSPENDED) {
            throw invalidState();
        }
        lifecycleState = PartyIdentifierLifecycleState.RETIRED;
        primary = false;
        touch();
    }

    public void changePrimary(boolean primary) {
        if (lifecycleState != PartyIdentifierLifecycleState.ACTIVE) {
            throw invalidState();
        }
        this.primary = primary;
        touch();
    }

    public void validateTemporalBounds() {
        if (validFrom != null && validTo != null && validTo.isBefore(validFrom)) {
            throw new IllegalArgumentException("MDM-003 INVALID_VALIDITY_WINDOW");
        }
    }

    private void requireState(PartyIdentifierLifecycleState expected) {
        if (lifecycleState != expected) {
            throw invalidState();
        }
    }

    private IllegalStateException invalidState() {
        return new IllegalStateException("MDM-004 IDENTIFIER_INVALID_STATE");
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MDM-005 REQUIRED_IDENTIFIER_FIELD: " + field);
        }
        return value;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
