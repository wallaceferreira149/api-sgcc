package dev.arcanus.api_sgcc.modules.ops_logs.dtos;

import jakarta.validation.constraints.NotBlank;

public record AircraftTypeRequestDto (
    @NotBlank
    String code
){
}
