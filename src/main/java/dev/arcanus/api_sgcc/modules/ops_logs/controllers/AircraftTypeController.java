package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.services.AircraftTypeService;
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
@RequestMapping("/api/v1/aircraft-types")
@Validated
public class AircraftTypeController {

    private final AircraftTypeService service;

    public AircraftTypeController(AircraftTypeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AircraftTypeResponseDto> createAircraftType(
        @RequestBody @Valid AircraftTypeRequestDto request,
        UriComponentsBuilder uriBuilder) {

        AircraftTypeResponseDto response = service.create(request);

        URI uri = uriBuilder.path("/api/v1/aircraft-types/{id}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AircraftTypeResponseDto>> getAllAircraftTypes() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftTypeResponseDto> getAircraftTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftTypeResponseDto> updateAircraftType(
        @PathVariable Long id,
        @RequestBody @Valid AircraftTypeRequestDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraftType(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
