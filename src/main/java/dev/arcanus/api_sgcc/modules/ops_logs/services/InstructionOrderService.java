package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.modules.ops_logs.mappers.InstructionOrderMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.InstructionOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class InstructionOrderService {

    private final InstructionOrderRepository repository;
    private final InstructionOrderMapper mapper;

    public InstructionOrderService(InstructionOrderRepository repository,
                                   InstructionOrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

}
