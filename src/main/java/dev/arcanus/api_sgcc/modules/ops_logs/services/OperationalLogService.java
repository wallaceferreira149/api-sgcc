package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCInvalidRequestException;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.FlightControlRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OperationalLogRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OperationalLogResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.*;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OperationalLogMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.*;
import dev.arcanus.api_sgcc.modules.ops_logs.value_objects.FlightControl;
import dev.arcanus.api_sgcc.modules.ops_logs.value_objects.FlightInfo;
import dev.arcanus.api_sgcc.modules.ops_logs.value_objects.IFF;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OperationalLogService {

    private final OperationalLogRepository opsLogepository;
    private final OperationalLogMapper opsLogMapper;
    private final OpsUserRepository opsUserRepository;
    private final OpsLocaleRepository opsLocaleRepository;
    private final InstructionOrderRepository instructionOrderRepository;
    private final OpsRoleRepository opsRoleRepository;
    private final AircraftTypeRepository aircraftTypeRepository;

    public OperationalLogService(
        OperationalLogRepository opsLogepository,
        OperationalLogMapper opsLogMapper,
        OpsUserRepository opsUserRepository,
        OpsLocaleRepository opsLocaleRepository,
        InstructionOrderRepository instructionOrderRepository,
        OpsRoleRepository opsRoleRepository,
        AircraftTypeRepository aircraftTypeRepository
        ) {
        this.opsLogepository = opsLogepository;
        this.opsLogMapper = opsLogMapper;
        this.opsUserRepository = opsUserRepository;
        this.opsLocaleRepository = opsLocaleRepository;
        this.instructionOrderRepository = instructionOrderRepository;
        this.opsRoleRepository = opsRoleRepository;
        this.aircraftTypeRepository = aircraftTypeRepository;
    }



    @Transactional
    public OperationalLogResponseDto create(OperationalLogRequestDto request) {

        Set<Long> opsUserIds = Set.of(
            request.operatorId(),
            request.assistantId(),
            request.instructorId()
        ).stream().filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, OpsUser> opsUserMap = opsUserRepository.findAllById(opsUserIds)
            .stream()
            .collect(Collectors.toMap(OpsUser::getId, user -> user));

        validateFound(opsUserMap, request.operatorId(), "Operador");
        validateFound(opsUserMap, request.assistantId(), "Assistente");
        validateFound(opsUserMap, request.instructorId(), "Instrutor");

        OpsLocale locale = opsLocaleRepository.findById(request.localeId())
            .orElseThrow(() -> new SGCCResourceNotFoundException("Localidade não encontrada"));

        InstructionOrder instructionOrder = instructionOrderRepository.findById(request.instructionOrderId())
            .orElseThrow(() -> new SGCCResourceNotFoundException("Ordem de instrução não encontrada"));

        OpsRole opsRole = opsRoleRepository.findById(request.opsRoleId())
            .orElseThrow(() -> new SGCCResourceNotFoundException("Qualificação operacional não encontrada"));

        FlightControl flightControl = null;
        if (Boolean.TRUE.equals(request.isReal())) {
            FlightControlRequestDto flighControlRequest = request.flightControlRequestDto();

            if (flighControlRequest == null) {
                throw new SGCCInvalidRequestException("As informações de controle do voo, são obrigatórias para voos reais");
            }

            AircraftType aircraftType = aircraftTypeRepository.findById(flighControlRequest.aircraftTypeId())
                .orElseThrow(() -> new SGCCResourceNotFoundException("Tipo de aeronave não encontrado"));

            flightControl = FlightControl.of(
                flighControlRequest.controlStartedAt(),
                flighControlRequest.controlEndAt(),
                FlightInfo.of(
                    flighControlRequest.aircraftCallsign(),
                    aircraftType,
                    flighControlRequest.aircraftQuantity(),
                    flighControlRequest.aircraftIFF()
                )
            );
        }



        OperationalLog operationalLog = OperationalLog.builder()
            .operator(opsUserMap.get(request.operatorId()))
            .assistant(opsUserMap.get(request.assistantId()))
            .instructor(opsUserMap.get(request.instructorId()))
            .locale(locale)
            .instructionOrder(instructionOrder)
            .opsRole(opsRole)
            .isReal(request.isReal())
            .quantityRegistered(request.quantityRegistered())
            .opslogDate(Instant.from(request.opsLogDate()))
            .flightControl(flightControl)
            .build();

        return opsLogMapper.toResponse(operationalLog);
    }

    private void validateFound(Map<Long, ?> map, Long id, String label) {
        if (id != null && !map.containsKey(id)) {
            throw new SGCCResourceNotFoundException(label + " não encontrado " + id);
        }
    }

}
