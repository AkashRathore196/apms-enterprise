package com.apms.mdm.party;

import com.apms.mdm.common.audit.AuditRepository;
import com.apms.mdm.common.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyIdentifierServiceTest {

    @Mock PartyRepository parties;
    @Mock PartyIdentifierRepository identifiers;
    @Mock AuditRepository auditRepository;
    @Mock OutboxRepository outboxRepository;

    @InjectMocks PartyIdentifierService service;

    @Test
    void createsIdentifierWithCanonicalTrimmedValue() {
        UUID partyId = UUID.randomUUID();
        Party party = new Party(PartyType.ORGANIZATION);
        when(parties.findById(partyId)).thenReturn(Optional.of(party));
        when(identifiers.existsByIdentifierTypeAndNormalizedIdentifierValueAndIssuingAuthorityAndIssuingJurisdictionAndLifecycleStateIn(
                eq("ENTERPRISE_ID"), eq("APMS-001"), isNull(), isNull(), any())).thenReturn(false);
        when(identifiers.countByPartyPartyIdAndIdentifierTypeAndPrimaryTrueAndLifecycleState(
                eq(partyId), eq("ENTERPRISE_ID"), eq(PartyIdentifierLifecycleState.ACTIVE))).thenReturn(0L);

        UUID identifierId = service.create(partyId, "ENTERPRISE_ID", " APMS-001 ", null, null, null,
                null, null, true);

        assertNotNull(identifierId);
        verify(identifiers).save(any(PartyIdentifier.class));
        verify(auditRepository).save(any());
        verify(outboxRepository).save(any());
    }

    @Test
    void rejectsDuplicateCurrentIdentifier() {
        UUID partyId = UUID.randomUUID();
        Party party = new Party(PartyType.ORGANIZATION);
        when(parties.findById(partyId)).thenReturn(Optional.of(party));
        when(identifiers.existsByIdentifierTypeAndNormalizedIdentifierValueAndIssuingAuthorityAndIssuingJurisdictionAndLifecycleStateIn(
                eq("ENTERPRISE_ID"), eq("APMS-001"), isNull(), isNull(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> service.create(partyId, "ENTERPRISE_ID", "APMS-001", null, null, null,
                        null, null, false));
        verify(identifiers, never()).save(any());
    }
}
