package com.apms.mdm.party;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartyLifecycleTest {
    @Test
    void organizationMovesThroughApprovalToActive() {
        Party party = new Party(PartyType.ORGANIZATION);
        assertEquals(LifecycleState.DRAFT, party.getLifecycleState());

        party.submit();
        assertEquals(LifecycleState.SUBMITTED, party.getLifecycleState());

        party.moveToReview();
        assertEquals(LifecycleState.REVIEW, party.getLifecycleState());

        party.approve();
        assertEquals(LifecycleState.APPROVED, party.getLifecycleState());

        party.activate();
        assertEquals(LifecycleState.ACTIVE, party.getLifecycleState());
    }

    @Test
    void approvalBeforeReviewIsRejected() {
        Party party = new Party(PartyType.ORGANIZATION);
        party.submit();
        assertThrows(IllegalStateException.class, party::approve);
    }
}
