package dev.arcanus.api_sgcc.modules.ops_logs.mappers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.InstructionOrder;
import org.springframework.stereotype.Component;

@Component
public class InstructionOrderMapper {

    public InstructionOrderResponseDto toResponse(InstructionOrder entity) {
        return new
    }

    public InstructionOrder toEntity(InstructionOrderRequestDto request) {
        
        return new InstructionOrder(
            request.code(),
            request.quantity(),
            request.description(),
            request.ops_role_id()
        );
    }
}
