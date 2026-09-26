package dev.arcanus.api_sgcc.modules.ops_logs.mappers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.AircraftType;
import org.springframework.stereotype.Component;

@Component
public class AircraftTypeMapper {

    public AircraftType toEntity(AircraftTypeRequestDto dto) {
        return new AircraftType(dto.code());
    }

    public AircraftTypeResponseDto toResponse(AircraftType entity) {
        return new AircraftTypeResponseDto(entity.getId(), entity.getCode());
    }

}
