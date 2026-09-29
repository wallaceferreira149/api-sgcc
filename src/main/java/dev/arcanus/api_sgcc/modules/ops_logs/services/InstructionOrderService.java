package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCInvalidRequestException;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.InstructionOrder;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.InstructionOrderMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.InstructionOrderRepository;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.OpsRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstructionOrderService {

    private final InstructionOrderRepository repository;
    private final InstructionOrderMapper mapper;
    private final OpsRoleRepository opsRoleRepository;

    public InstructionOrderService(InstructionOrderRepository repository,
                                   InstructionOrderMapper mapper,
                                   OpsRoleRepository opsRoleRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.opsRoleRepository = opsRoleRepository;
    }

    @Transactional
    public InstructionOrderResponseDto create(InstructionOrderRequestDto request) {

        InstructionOrder entity = mapper.toEntity(request);

        if (repository.existsByCode(entity.getCode())) {
            throw new SGCCResourceAlreadyExists("O código da Ordem de Instrunção já foi cadastrado");
        }

        OpsRole opsRole = opsRoleRepository.findById(request.opsRoleId())
            .orElseThrow(() -> new SGCCResourceNotFoundException("Qualificação Operacional não encontrada"));

        entity.assignRole(opsRole);

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<InstructionOrderResponseDto> findAll() {
        return repository.findAll().stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public InstructionOrderResponseDto findById(Long id) {
        if (id == null) {
            throw new SGCCInvalidRequestException("O Id da Ordem de Instrução deve estar presente");
        }
        return mapper.toResponse(repository.findById(id)
            .orElseThrow(() -> new SGCCResourceNotFoundException("Ordem de Instrunção não encontrada")));
    }

    @Transactional
    public InstructionOrderResponseDto update(Long id, InstructionOrderRequestDto request) {
        if (id == null) {
            throw new SGCCInvalidRequestException("O Id da Ordem de Instrução deve estar presente");
        }

        InstructionOrder entity = repository.findById(id)
            .orElseThrow(() -> new SGCCResourceNotFoundException("Ordem de Instrunção não encontrada"));

        entity.updateDetails(
            request.code(),
            request.quantity(),
            request.description()
        );

        if (request.opsRoleId() != entity.getRoleFor().getId()) {
            OpsRole opsRole = opsRoleRepository.findById(request.opsRoleId())
                .orElseThrow(() -> new SGCCResourceNotFoundException("Qualificação Operacional não encontrada"));
            entity.assignRole(opsRole);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new SGCCInvalidRequestException("O Id da Ordem de Instrução deve estar presente");
        }
        if (!repository.existsById(id)) {
            throw new SGCCResourceNotFoundException("Ordem de Instrução não encontrada");
        }
        repository.deleteById(id);
    }

}
