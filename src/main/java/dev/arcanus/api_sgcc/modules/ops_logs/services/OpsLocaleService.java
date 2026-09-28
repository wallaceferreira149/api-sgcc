package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCInvalidRequestException;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsLocale;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsLocaleMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.OpsLocaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class OpsLocaleService {

    private final OpsLocaleRepository repository;
    private final OpsLocaleMapper mapper;

    public OpsLocaleService(OpsLocaleRepository repository, OpsLocaleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public OpsLocaleResponseDto create(OpsLocaleRequestDto request) {

        OpsLocale entity = mapper.toEntity(request);

        if (repository.existsByLocale(entity.getLocale())) {
            throw new SGCCInvalidRequestException("A localidade já existe. Não é possível criar com o mesmo nome.");
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<OpsLocaleResponseDto> listAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OpsLocaleResponseDto findById(Long id) {
        if (id == null) {
            throw new SGCCInvalidRequestException("O Id da localidade tem que estar presente");
        }

        return mapper.toResponse(
            repository.findById(id)
                .orElseThrow(
                    () -> new SGCCResourceNotFoundException("Localidade não encontrada"))
        );
    }

    @Transactional
    public OpsLocaleResponseDto update(Long id, OpsLocaleRequestDto request) {
        if  (request == null || id == null) {
            throw new SGCCInvalidRequestException("Dados de localidade inválidos. Insira o Id e o nome da localidade");
        }

        OpsLocale localeToUpdate = mapper.toEntity(request);

        OpsLocale locale = repository.findById(id)
                .orElseThrow(()-> new SGCCResourceNotFoundException("Localidade não encontrada"));

        locale.update(localeToUpdate.getLocale());

        return mapper.toResponse(repository.save(locale));
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new SGCCInvalidRequestException("O Id deve estar presente");
        }

        repository.findById(id)
            .orElseThrow(()-> new SGCCResourceNotFoundException("Localidade não encontrada"));

        repository.deleteById(id);
    }

    private String normalizeAndValidadeLocale(String locale) {
        if (locale == null || locale.isBlank()) {
            throw new SGCCInvalidRequestException("A localidade não pode ser vazia");
        }

        return locale.trim().toUpperCase(Locale.ROOT);
    }


}
