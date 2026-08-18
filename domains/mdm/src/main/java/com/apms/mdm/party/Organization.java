package com.apms.mdm.party;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "organization")
public class Organization {
    @Id
    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId = UUID.randomUUID();

    @OneToOne(optional = false)
    @JoinColumn(name = "party_id", nullable = false, unique = true)
    private Party party;

    @Column(name = "legal_name", nullable = false, length = 300)
    private String legalName;

    @Column(name = "display_name", length = 300)
    private String displayName;

    protected Organization() {}

    public Organization(Party party, String legalName, String displayName) {
        this.party = party;
        this.legalName = legalName;
        this.displayName = displayName;
    }

    public UUID getOrganizationId() { return organizationId; }
    public UUID getPartyId() { return party.getPartyId(); }
    public String getLegalName() { return legalName; }
    public String getDisplayName() { return displayName; }
}
