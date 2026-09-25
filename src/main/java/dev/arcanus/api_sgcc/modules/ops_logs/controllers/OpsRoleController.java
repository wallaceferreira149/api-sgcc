package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsRoleMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.services.OpsRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ops-roles")
@Validated
public class OpsRoleController {

    private final OpsRoleService opsRoleService;
    private final OpsRoleMapper mapper;

    public OpsRoleController(OpsRoleService opsRoleService, OpsRoleMapper mapper) {
        this.opsRoleService = opsRoleService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<OpsRoleResponseDto> createOpsRole(
        @RequestBody @Valid OpsRoleRequestDto body,
        UriComponentsBuilder uriBuilder
    ) {
        OpsRoleResponseDto opsRoleCreatedDto = opsRoleService.create(body);

        URI uri = uriBuilder.path("/api/v1/ops-roles/{id}")
            .buildAndExpand(opsRoleCreatedDto.id()).toUri();

        return ResponseEntity.created(uri).body(opsRoleCreatedDto);
    }

    @GetMapping
    public ResponseEntity<List<OpsRoleResponseDto>> findAllOpsRole() {
        List<OpsRoleResponseDto> allOpsRole = opsRoleService.findAll().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(allOpsRole);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpsRoleResponseDto> findOpsRoleById(
        @PathVariable @Min(value = 1, message = "O Id da qualificação operacional é obrigatório") Long id
    ){
        OpsRole opsRole = opsRoleService.findById(id);
        return ResponseEntity.ok(mapper.toResponse(opsRole));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpsRoleResponseDto> updateOpsRole(
        @PathVariable @Min(value = 1, message = "O Id da qualificação operacional é obrigatório") Long id,
        @RequestBody @Valid OpsRoleRequestDto request
    ) {
        return ResponseEntity.ok(opsRoleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOpsRole(
        @PathVariable @Min(value = 1, message = "O Id da qualificação operacional é obrigatório") Long id
    ) {
        opsRoleService.delete(id);
    }


}
