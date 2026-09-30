package dev.arcanus.api_sgcc.modules.ops_logs.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

public record FlightControlRequestDto (

    @NotBlank
    Instant controlStartedAt,

    @NotBlank
    Instant controlEndAt,

    @NotBlank
    String aircraftCallsign,

    @NotBlank
    Long aircraftTypeId,

    @Min(1)
    int aircraftQuantity,

    @NotBlank
    @Pattern(regexp = "^[0-7]{4}$")
    String aircraftIFF
) {
}
