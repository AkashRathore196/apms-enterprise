package com.apms.mdm.party;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PartyIdentifierTest {

    @Test
    void createsActiveIdentifierWithValidTemporalWindow() {
        Party party = new Party(PartyType.ORGANIZATION);
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-12-31T23:59:59Z");

        PartyIdentifier identifier = new PartyIdentifier(
                party,
                "ENTERPRISE_ID",
                "APMS-001",
                "APMS-001",
                null,
                null,
                null,
                from,
                to,
                true);

        assertEquals(PartyIdentifierLifecycleState.ACTIVE, identifier.getLifecycleState());
        assertTrue(identifier.isPrimary());
        assertEquals("APMS-001", identifier.getNormalizedIdentifierValue());
    }

    @Test
    void rejectsInvalidTemporalWindow() {
        Party party = new Party(PartyType.ORGANIZATION);

        assertThrows(IllegalArgumentException.class, () -> new PartyIdentifier(
                party,
                "ENTERPRISE_ID",
                "APMS-001",
                "APMS-001",
                null,
                null,
                null,
                Instant.parse("2026-12-31T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z"),
                false));
    }

    @Test
    void retirementClearsPrimaryDesignation() {
        Party party = new Party(PartyType.ORGANIZATION);
        PartyIdentifier identifier = new PartyIdentifier(
                party, "ENTERPRISE_ID", "APMS-001", "APMS-001",
                null, null, null, null, null, true);

        identifier.retire();

        assertEquals(PartyIdentifierLifecycleState.RETIRED, identifier.getLifecycleState());
        assertFalse(identifier.isPrimary());
    }

    @Test
    void suspendThenExpireIsSupported() {
        Party party = new Party(PartyType.ORGANIZATION);
        PartyIdentifier identifier = new PartyIdentifier(
                party, "ENTERPRISE_ID", "APMS-001", "APMS-001",
                null, null, null, null, null, false);

        identifier.suspend();
        assertEquals(PartyIdentifierLifecycleState.SUSPENDED, identifier.getLifecycleState());

        identifier.expire();
        assertEquals(PartyIdentifierLifecycleState.EXPIRED, identifier.getLifecycleState());
    }
}
