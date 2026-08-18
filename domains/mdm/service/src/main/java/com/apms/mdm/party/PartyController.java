package com.apms.mdm.party;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parties")
public class PartyController {
    private final PartyService service;

    public PartyController(PartyService service) { this.service = service; }

    public record CreateOrganizationRequest(@NotBlank String legalName, String displayName) {}
    public record PartyResponse(UUID partyId, UUID organizationId, String lifecycleState, long version) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyResponse create(@Valid @RequestBody CreateOrganizationRequest request) {
        return service.createOrganization(request);
    }

    @PostMapping("/{id}/submit")
    public PartyResponse submit(@PathVariable UUID id) { return service.submit(id); }

    @PostMapping("/{id}/approve")
    public PartyResponse approve(@PathVariable UUID id) { return service.approve(id); }
}
