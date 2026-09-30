package dev.arcanus.api_sgcc.modules.ops_logs.dtos;

import java.time.Instant;

public record FlightControlResponseDto (
    Instant controlStartedAt,
    Instant controlEndAt,
    String aircraftCallsign,
    String aircraftType,
    int aircraftQuantity,
    String aircraftIFF
) {
}
