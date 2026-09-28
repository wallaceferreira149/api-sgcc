package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.OpsLocaleRepository;
import dev.arcanus.api_sgcc.modules.ops_logs.services.OpsLocaleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ops-locale")
@Validated
public class OpsLocaleController {

    private final OpsLocaleService service;

    public OpsLocaleController(OpsLocaleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OpsLocaleResponseDto> createLocale(
        @Valid @RequestBody OpsLocaleRequestDto request,
        UriComponentsBuilder uriBuilder
    ) {
        OpsLocaleResponseDto localeResponseDto = service.create(request);
        URI uri = uriBuilder.path("{id}")
            .buildAndExpand(localeResponseDto.id())
            .toUri();
        return ResponseEntity.created(uri).body(localeResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<OpsLocaleResponseDto>> getAllLocale(){
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpsLocaleResponseDto> getLocaleById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpsLocaleResponseDto> updateLocale(
        @PathVariable Long id,
        @Valid @RequestBody OpsLocaleRequestDto request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocale(
        @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
