package com.apms.mdm.party;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartyIdentifierController.class)
class PartyIdentifierControllerTest {
    @Autowired MockMvc mockMvc;

    @MockBean PartyIdentifierService service;

    @Test
    void createsIdentifier() throws Exception {
        UUID partyId = UUID.randomUUID();
        UUID identifierId = UUID.randomUUID();
        when(service.create(eq(partyId), eq("ENTERPRISE_ID"), eq("APMS-001"),
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(true)))
                .thenReturn(identifierId);

        mockMvc.perform(post("/api/v1/parties/{partyId}/identifiers", partyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifierType\":\"ENTERPRISE_ID\",\"identifierValue\":\"APMS-001\",\"primary\":true}"))
                .andExpect(status().isCreated());
    }
}
