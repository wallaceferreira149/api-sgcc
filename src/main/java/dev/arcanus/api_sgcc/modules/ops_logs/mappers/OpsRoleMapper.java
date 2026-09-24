package dev.arcanus.api_sgcc.modules.ops_logs.mappers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import org.springframework.stereotype.Component;


@Component
public class OpsRoleMapper {

    public OpsRoleResponseDto toResponse(OpsRole entity) {
        String description = entity.getDescription() == null ? "" : entity.getDescription();
        return new OpsRoleResponseDto(entity.getName(), description);
    }

    public OpsRole toEntity(OpsRoleRequestDto dto) {
        return new OpsRole(
            dto.name(),
            dto.description(),
            dto.daysToExpire()
        );
    }
}
