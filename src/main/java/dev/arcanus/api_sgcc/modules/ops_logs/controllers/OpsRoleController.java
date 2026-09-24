package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsRoleMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.services.OpsRoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/opsroles")
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
        OpsRole opsRole = opsRoleService.create(body);

        URI uri = uriBuilder.path("/api/v1/opsroles/{id}")
            .buildAndExpand(opsRole.getId()).toUri();

        return ResponseEntity.created(uri).body(mapper.toResponse(opsRole));

        }
}
