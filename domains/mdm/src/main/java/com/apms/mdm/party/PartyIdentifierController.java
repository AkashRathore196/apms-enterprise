package com.apms.mdm.party;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parties/{partyId}/identifiers")
public class PartyIdentifierController {
    private final PartyIdentifierService service;

    public PartyIdentifierController(PartyIdentifierService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<IdentifierResponse> create(
            @PathVariable UUID partyId,
            @Valid @RequestBody CreateIdentifierRequest request) {
        UUID identifierId = service.create(
                partyId,
                request.identifierType(),
                request.identifierValue(),
                request.issuingAuthority(),
                request.issuingJurisdiction(),
                request.sourceSystem(),
                request.validFrom(),
                request.validTo(),
                request.primary());
        return ResponseEntity.created(URI.create("/api/v1/parties/" + partyId + "/identifiers/" + identifierId))
                .body(new IdentifierResponse(identifierId, partyId));
    }

    @PostMapping("/{identifierId}/suspend")
    public ResponseEntity<Void> suspend(@PathVariable UUID partyId, @PathVariable UUID identifierId) {
        service.suspend(identifierId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identifierId}/expire")
    public ResponseEntity<Void> expire(@PathVariable UUID partyId, @PathVariable UUID identifierId) {
        service.expire(identifierId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identifierId}/retire")
    public ResponseEntity<Void> retire(@PathVariable UUID partyId, @PathVariable UUID identifierId) {
        service.retire(identifierId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{identifierId}/primary")
    public ResponseEntity<Void> setPrimary(
            @PathVariable UUID partyId,
            @PathVariable UUID identifierId,
            @Valid @RequestBody PrimaryRequest request) {
        service.setPrimary(identifierId, request.primary());
        return ResponseEntity.noContent().build();
    }

    public record CreateIdentifierRequest(
            @NotBlank String identifierType,
            @NotBlank String identifierValue,
            String issuingAuthority,
            String issuingJurisdiction,
            String sourceSystem,
            Instant validFrom,
            Instant validTo,
            boolean primary) {}

    public record PrimaryRequest(@NotNull Boolean primary) {}

    public record IdentifierResponse(UUID identifierId, UUID partyId) {}
}
