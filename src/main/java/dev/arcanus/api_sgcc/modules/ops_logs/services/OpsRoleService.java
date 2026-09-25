package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsRoleMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.OpsRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OpsRoleService {

    private final OpsRoleRepository repository;
    private final OpsRoleMapper mapper;

    public OpsRoleService(OpsRoleRepository repository, OpsRoleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public OpsRoleResponseDto create(OpsRoleRequestDto request) {

        String normalizedName = request.name().trim().toUpperCase();

        if (repository.existsByNameIgnoreCase(normalizedName)) {
            throw new SGCCResourceAlreadyExists("Essa qualificação operacional já foi cadastrada");
        }

        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public List<OpsRole> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public OpsRole findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new SGCCResourceNotFoundException(
                                "Qualificação operacional não encontrada"
                        )
                );
    }

    @Transactional
    public OpsRoleResponseDto update(Long id, OpsRoleRequestDto request) {

        OpsRole opsRoleEntity = repository.findById(id)
            .orElseThrow(() -> new SGCCResourceNotFoundException("Qualificação operacional não encontrada"));

        opsRoleEntity.updateDetails(
            request.name(),
            request.description(),
            request.daysToExpire()
        );

        OpsRole updatedOpsRole = repository.save(opsRoleEntity);

        return mapper.toResponse(updatedOpsRole);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new SGCCResourceNotFoundException("Qualificação operacional não encontrada");
        }
        repository.deleteById(id);
    }

}
