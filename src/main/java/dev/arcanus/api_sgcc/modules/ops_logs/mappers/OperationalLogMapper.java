package dev.arcanus.api_sgcc.modules.ops_logs.mappers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.FlightControlResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OperationalLogRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OperationalLogResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OperationalLog;
import org.springframework.stereotype.Component;

@Component
public class OperationalLogMapper {

    public OperationalLogResponseDto toResponse(OperationalLog operationalLog) {
        return new OperationalLogResponseDto(
            operationalLog.getId(),
            operationalLog.getOperator().getId(),
            operationalLog.getOperator().getEmail(),
            operationalLog.getAssistant().getId(),
            operationalLog.getAssistant().getEmail(),
            operationalLog.getInstructor().getId(),
            operationalLog.getInstructor().getEmail(),
            operationalLog.getLocale().getId(),
            operationalLog.getLocale().getLocale(),
            operationalLog.getInstructionOrder().getId(),
            operationalLog.getInstructionOrder().getCode(),
            operationalLog.getOpsRole().getId(),
            operationalLog.getOpsRole().getName(),
            operationalLog.getOpslogDate(),
            operationalLog.isReal(),
            operationalLog.getQuantityRegistered(),
            new FlightControlResponseDto(
                operationalLog.getFlightControl().getControlStartedAt(),
                operationalLog.getFlightControl().getControlEndAt(),
                operationalLog.getFlightControl().getFlightInfo().getAircraftCallsign(),
                operationalLog.getFlightControl().getFlightInfo().getAircraftType().getCode(),
                operationalLog.getFlightControl().getFlightInfo().getAircraftQuantity(),
                operationalLog.getFlightControl().getFlightInfo().getAircraftIFF().getCode()
            )
        );
    }
}
