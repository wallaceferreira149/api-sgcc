package dev.arcanus.api_sgcc.modules.ops_logs.mappers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsLocale;

public class OpsLocaleMapper {

    public OpsLocaleResponseDto toResponse(OpsLocale opsLocale) {
        return new OpsLocaleResponseDto(opsLocale.getId(), opsLocale.getLocale());
    }

    public OpsLocale toEntity(OpsLocaleRequestDto request) {
        return new OpsLocale(request.locale());
    }
}
