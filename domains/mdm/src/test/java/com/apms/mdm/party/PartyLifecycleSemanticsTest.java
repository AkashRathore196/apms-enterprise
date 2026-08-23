package com.apms.mdm.party;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PartyLifecycleSemanticsTest {

    @Test
    void followsCanonicalApprovalThenActivationLifecycle() throws InterruptedException {
        Party party = new Party(PartyType.ORGANIZATION);
        Instant initialUpdate = party.getUpdatedAt();

        party.submit();
        assertEquals(LifecycleState.SUBMITTED, party.getLifecycleState());

        party.moveToReview();
        assertEquals(LifecycleState.REVIEW, party.getLifecycleState());

        party.approve();
        assertEquals(LifecycleState.APPROVED, party.getLifecycleState());

        party.activate();
        assertEquals(LifecycleState.ACTIVE, party.getLifecycleState());
        assertTrue(!party.getUpdatedAt().isBefore(initialUpdate));
    }

    @Test
    void rejectsSkippingApproval() {
        Party party = new Party(PartyType.ORGANIZATION);
        party.submit();
        party.moveToReview();

        assertThrows(IllegalStateException.class, party::activate);
        assertEquals(LifecycleState.REVIEW, party.getLifecycleState());
    }

    @Test
    void supportsSuspendReactivateAndRetire() {
        Party party = new Party(PartyType.ORGANIZATION);
        party.submit();
        party.moveToReview();
        party.approve();
        party.activate();

        party.suspend();
        assertEquals(LifecycleState.SUSPENDED, party.getLifecycleState());

        party.reactivate();
        assertEquals(LifecycleState.ACTIVE, party.getLifecycleState());

        party.retire();
        assertEquals(LifecycleState.RETIRED, party.getLifecycleState());
    }
}
