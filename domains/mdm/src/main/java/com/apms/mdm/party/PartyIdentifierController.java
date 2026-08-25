package com.apms.mdm.party;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
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
            @PathVariable("partyId") UUID partyId,
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
                .body(new IdentifierResponse(identifierId, partyId, request.identifierType(), request.identifierValue(),
                        request.issuingAuthority(), request.issuingJurisdiction(), request.sourceSystem(),
                        request.validFrom(), request.validTo(), request.primary(), PartyIdentifierLifecycleState.ACTIVE, null,
                        null, null));
    }

    @GetMapping
    public ResponseEntity<List<IdentifierResponse>> list(@PathVariable("partyId") UUID partyId) {
        List<IdentifierResponse> responses = service.list(partyId).stream()
                .map(identifier -> new IdentifierResponse(
                        identifier.getIdentifierId(),
                        identifier.getParty().getPartyId(),
                        identifier.getIdentifierType(),
                        identifier.getIdentifierValue(),
                        identifier.getIssuingAuthority(),
                        identifier.getIssuingJurisdiction(),
                        identifier.getSourceSystem(),
                        identifier.getValidFrom(),
                        identifier.getValidTo(),
                        identifier.isPrimary(),
                        identifier.getLifecycleState(),
                        identifier.getVersion(),
                        identifier.getCreatedAt(),
                        identifier.getUpdatedAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{identifierId}/suspend")
    public ResponseEntity<Void> suspend(@PathVariable("partyId") UUID partyId, @PathVariable("identifierId") UUID identifierId) {
        service.suspend(identifierId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identifierId}/expire")
    public ResponseEntity<Void> expire(@PathVariable("partyId") UUID partyId, @PathVariable("identifierId") UUID identifierId) {
        service.expire(identifierId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identifierId}/retire")
    public ResponseEntity<Void> retire(@PathVariable("partyId") UUID partyId, @PathVariable("identifierId") UUID identifierId) {
        service.retire(identifierId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{identifierId}/primary")
    public ResponseEntity<Void> setPrimary(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("identifierId") UUID identifierId,
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

    public record IdentifierResponse(
            UUID identifierId,
            UUID partyId,
            String identifierType,
            String identifierValue,
            String issuingAuthority,
            String issuingJurisdiction,
            String sourceSystem,
            Instant validFrom,
            Instant validTo,
            boolean primary,
            PartyIdentifierLifecycleState lifecycleState,
            Long version,
            Instant createdAt,
            Instant updatedAt) {}
}
