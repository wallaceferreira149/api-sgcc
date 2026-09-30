package dev.arcanus.api_sgcc.modules.ops_logs.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record OperationalLogRequestDto (
    @NotBlank
    Long operatorId,

    Long assistantId,

    Long instructorId,

    @NotBlank
    Long localeId,

    @NotBlank
    Long instructionOrderId,

    @NotBlank
    Long opsRoleId,

    @NotBlank
    Instant opsLogDate,

    @NotBlank
    boolean isReal,

    @Min(1)
    int quantityRegistered,

    FlightControlRequestDto flightControlRequestDto
) {
}
