package dev.arcanus.api_sgcc.modules.ops_logs.mappers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.InstructionOrder;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import org.springframework.stereotype.Component;

@Component
public class InstructionOrderMapper {

    public InstructionOrderResponseDto toResponse(InstructionOrder entity) {
        OpsRole opsRole = entity.getRoleFor();
        return new InstructionOrderResponseDto(
            entity.getId(),
            entity.getCode(),
            entity.getQuantity(),
            entity.getDescription(),
            opsRole != null ? opsRole.getId() : null,
            opsRole != null ? opsRole.getName() : null
        );
    }

    public InstructionOrder toEntity(InstructionOrderRequestDto request) {
        return new InstructionOrder(
            request.code(),
            request.quantity(),
            request.description()
        );
    }
}
