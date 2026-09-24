package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsRoleMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.OpsRoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OpsRoleService {

    private final OpsRoleRepository repository;
    private final OpsRoleMapper mapper;

    public OpsRoleService(OpsRoleRepository repository, OpsRoleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public OpsRole create(OpsRoleRequestDto dto) {

        String normalizedName = dto.name().trim().toUpperCase();

        if (repository.existsByNameIgnoreCase(normalizedName)) {
            throw new SGCCResourceAlreadyExists("Essa qualificação operacional já foi cadastrada");
        }

        return repository.save(mapper.toEntity(dto));
    }

}
