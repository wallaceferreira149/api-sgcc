package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCInvalidRequestException;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.AircraftType;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.AircraftTypeMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.AircraftTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AircraftTypeService {

    private static final String ALREADY_EXISTS_MESSAGE =
        "O tipo de aeronave já existe. Utilize o código canônico no formato A-29.";

    private final AircraftTypeRepository repository;
    private final AircraftTypeMapper mapper;

    public AircraftTypeService(AircraftTypeRepository repository, AircraftTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public AircraftTypeResponseDto create(AircraftTypeRequestDto request) {

        String codeNormalized = normalizeAndValidateCode(request.code());

        if (repository.existsByCodeKey(codeKey(codeNormalized))) {
            throw new SGCCResourceAlreadyExists(ALREADY_EXISTS_MESSAGE);
        }

        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public List<AircraftTypeResponseDto> findAll() {
        return repository.findAll().stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public AircraftTypeResponseDto findById(Long id) {
        return mapper.toResponse(
            repository.findById(id)
                .orElseThrow( () -> new SGCCResourceNotFoundException("O tipo de aeronave não foi encontrado."))
        );
    }

    @Transactional
    public AircraftTypeResponseDto update(Long id, AircraftTypeRequestDto request) {
        AircraftType aircraftTypeEntity = repository.findById(id)
            .orElseThrow( () -> new SGCCResourceNotFoundException("O tipo de aeronave não foi encontrado."));

        String codeNormalized = normalizeAndValidateCode(request.code());

        if (repository.existsByCodeKeyAndIdNot(codeKey(codeNormalized), id)) {
            throw new SGCCResourceAlreadyExists(ALREADY_EXISTS_MESSAGE);
        }

        aircraftTypeEntity.update(codeNormalized);

        return mapper.toResponse(repository.save(aircraftTypeEntity));
    }

    @Transactional
    public void delete(Long id) {
        repository.findById(id)
            .orElseThrow( () -> new SGCCResourceNotFoundException("O tipo de aeronave não foi encontrado."));
        repository.deleteById(id);
    }

    private String normalizeAndValidateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new SGCCInvalidRequestException("O tipo de aeronave não pode ser vazio.");
        }

        return code.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Chave de comparacao do codigo: "A-29", "A29" e "A 29" viram a mesma chave.
     * Precisa espelhar os separadores removidos em
     * {@link AircraftTypeRepository#existsByCodeKey(String)}.
     */
    private String codeKey(String codeNormalized) {
        return codeNormalized.replace("-", "").replace(" ", "");
    }

}
