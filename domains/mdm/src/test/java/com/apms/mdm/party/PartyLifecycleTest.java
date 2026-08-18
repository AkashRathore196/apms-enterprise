package com.apms.mdm.party;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartyLifecycleTest {
    @Test
    void organizationMovesFromDraftToActive() {
        Party party = new Party(PartyType.ORGANIZATION);
        assertEquals(LifecycleState.DRAFT, party.getLifecycleState());

        party.submit();
        assertEquals(LifecycleState.SUBMITTED, party.getLifecycleState());

        party.approve();
        assertEquals(LifecycleState.ACTIVE, party.getLifecycleState());
    }

    @Test
    void approvalBeforeSubmissionIsRejected() {
        Party party = new Party(PartyType.ORGANIZATION);
        assertThrows(IllegalStateException.class, party::approve);
    }
}
