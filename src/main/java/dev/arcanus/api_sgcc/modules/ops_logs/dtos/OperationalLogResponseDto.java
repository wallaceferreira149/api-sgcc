package dev.arcanus.api_sgcc.modules.ops_logs.dtos;

import java.time.Instant;

public record OperationalLogResponseDto (
    Long id,
    Long operatorId,
    String operatorName,
    Long assistantId,
    String assistantName,
    Long instructorId,
    String instructorName,
    Long localeId,
    String localeName,
    Long instructionOrderId,
    String instructionOrderCode,
    Long opsRoleId,
    String opsRoleName,
    Instant opslogDate,
    boolean isReal,
    int quantityRegistered,
    FlightControlResponseDto flightControl,
) {
}
