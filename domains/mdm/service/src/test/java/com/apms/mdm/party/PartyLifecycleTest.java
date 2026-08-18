package com.apms.mdm.party;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PartyLifecycleTest {
    @Test
    void organizationStartsAsDraftAndCanBeSubmittedAndApproved() {
        Party party = new Party(PartyType.ORGANIZATION);

        assertEquals(LifecycleState.DRAFT, party.getLifecycleState());
        party.submit();
        assertEquals(LifecycleState.SUBMITTED, party.getLifecycleState());
        party.approve();
        assertEquals(LifecycleState.ACTIVE, party.getLifecycleState());
    }

    @Test
    void approvalFromDraftIsRejected() {
        Party party = new Party(PartyType.ORGANIZATION);
        assertThrows(IllegalStateException.class, party::approve);
    }
}
