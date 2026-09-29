package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCInvalidRequestException;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsLocale;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsLocaleMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.OpsLocaleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OpsLocaleService - Testes unitários")
class OpsLocaleServiceTest {

    @Mock
    private OpsLocaleRepository repository;

    @Mock
    private OpsLocaleMapper mapper;

    @InjectMocks
    private OpsLocaleService service;

    private OpsLocale localeWithId(String locale, Long id) {
        OpsLocale entity = new OpsLocale(locale);
        setId(entity, id);
        return entity;
    }

    private void setId(OpsLocale entity, Long id) {
        try {
            Field field = entity.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID via reflection", e);
        }
    }

    @Nested
    @DisplayName("UseCase: Create OpsLocale")
    class Create {

        @Test
        @DisplayName("Deve criar localidade com sucesso")
        void shouldCreateLocaleSuccessfully() {
            // Given
            var request = new OpsLocaleRequestDto("ICEA");
            var entity = new OpsLocale("ICEA");
            var savedEntity = localeWithId("ICEA", 1L);
            var response = new OpsLocaleResponseDto(1L, "ICEA");

            when(mapper.toEntity(request)).thenReturn(entity);
            when(repository.existsByLocale("ICEA")).thenReturn(false);
            when(repository.save(entity)).thenReturn(savedEntity);
            when(mapper.toResponse(savedEntity)).thenReturn(response);

            // When
            OpsLocaleResponseDto result = service.create(request);

            // Then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.locale()).isEqualTo("ICEA");
            verify(repository).existsByLocale("ICEA");
            verify(repository).save(entity);
            verify(mapper).toResponse(savedEntity);
        }

        @Test
        @DisplayName("Deve lançar exceção quando localidade já existe")
        void shouldThrowWhenLocaleAlreadyExists() {
            // Given
            var request = new OpsLocaleRequestDto("ICEA");

            when(mapper.toEntity(request)).thenReturn(new OpsLocale("ICEA"));
            when(repository.existsByLocale("ICEA")).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(SGCCInvalidRequestException.class)
                .hasMessageContaining("já existe");

            verify(repository).existsByLocale("ICEA");
        }

        @Test
        @DisplayName("Deve normalizar localidade para maiúsculas")
        void shouldNormalizeLocaleToUpperCase() {
            // Given
            var request = new OpsLocaleRequestDto("icea");
            var entity = new OpsLocale("ICEA");
            var savedEntity = localeWithId("ICEA", 1L);
            var response = new OpsLocaleResponseDto(1L, "ICEA");

            when(mapper.toEntity(request)).thenReturn(entity);
            when(repository.existsByLocale("ICEA")).thenReturn(false);
            when(repository.save(entity)).thenReturn(savedEntity);
            when(mapper.toResponse(savedEntity)).thenReturn(response);

            // When
            OpsLocaleResponseDto result = service.create(request);

            // Then
            assertThat(result.locale()).isEqualTo("ICEA");
            verify(repository).existsByLocale("ICEA");
        }
    }

    @Nested
    @DisplayName("UseCase: Listar todas localidades")
    class ListAll {

        @Test
        @DisplayName("Deve retornar lista vazia quando não há localidades")
        void shouldReturnEmptyListWhenNoLocales() {
            // Given
            when(repository.findAll()).thenReturn(List.of());

            // When
            List<OpsLocaleResponseDto> result = service.listAll();

            // Then
            assertThat(result).isEmpty();
            verify(repository).findAll();
        }

        @Test
        @DisplayName("Deve retornar todas as localidades")
        void shouldReturnAllLocales() {
            // Given
            var entity1 = localeWithId("ICEA", 1L);
            var entity2 = localeWithId("SÃO PAULO", 2L);
            var response1 = new OpsLocaleResponseDto(1L, "ICEA");
            var response2 = new OpsLocaleResponseDto(2L, "SÃO PAULO");

            when(repository.findAll()).thenReturn(List.of(entity1, entity2));
            when(mapper.toResponse(entity1)).thenReturn(response1);
            when(mapper.toResponse(entity2)).thenReturn(response2);

            // When
            List<OpsLocaleResponseDto> result = service.listAll();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).locale()).isEqualTo("ICEA");
            assertThat(result.get(1).locale()).isEqualTo("SÃO PAULO");
            verify(repository).findAll();
            verify(mapper).toResponse(entity1);
            verify(mapper).toResponse(entity2);
        }
    }

    @Nested
    @DisplayName("UseCase: Listar localidade por ID")
    class FindById {

        @Test
        @DisplayName("Deve retornar localidade quando encontrada")
        void shouldReturnLocaleWhenFound() {
            // Given
            var entity = localeWithId("ICEA", 1L);
            var response = new OpsLocaleResponseDto(1L, "ICEA");

            when(repository.findById(1L)).thenReturn(Optional.of(entity));
            when(mapper.toResponse(entity)).thenReturn(response);

            // When
            OpsLocaleResponseDto result = service.findById(1L);

            // Then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.locale()).isEqualTo("ICEA");
            verify(repository).findById(1L);
            verify(mapper).toResponse(entity);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID é nulo")
        void shouldThrowWhenIdIsNull() {
            // When / Then
            assertThatThrownBy(() -> service.findById(null))
                .isInstanceOf(SGCCInvalidRequestException.class)
                .hasMessageContaining("tem que estar presente");
        }

        @Test
        @DisplayName("Deve lançar exceção quando localidade não encontrada")
        void shouldThrowWhenLocaleNotFound() {
            // Given
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(SGCCResourceNotFoundException.class)
                .hasMessageContaining("Recurso não encontrado");

            verify(repository).findById(999L);
        }
    }

    @Nested
    @DisplayName("UseCase: Atualizar localidade")
    class Update {

        @Test
        @DisplayName("Deve atualizar localidade com sucesso")
        void shouldUpdateLocaleSuccessfully() {
            // Given
            var request = new OpsLocaleRequestDto("BRASÍLIA");
            var existingEntity = localeWithId("ICEA", 1L);
            var localeToUpdate = new OpsLocale("BRASÍLIA");
            var response = new OpsLocaleResponseDto(1L, "BRASÍLIA");

            when(mapper.toEntity(request)).thenReturn(localeToUpdate);
            when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));
            when(repository.save(existingEntity)).thenReturn(existingEntity);
            when(mapper.toResponse(existingEntity)).thenReturn(response);

            // When
            OpsLocaleResponseDto result = service.update(1L, request);

            // Then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.locale()).isEqualTo("BRASÍLIA");
            verify(mapper).toEntity(request);
            verify(repository).findById(1L);
            verify(repository).save(existingEntity);
            verify(mapper).toResponse(existingEntity);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID é nulo")
        void shouldThrowWhenIdIsNull() {
            // Given
            var request = new OpsLocaleRequestDto("BRASÍLIA");

            // When / Then
            assertThatThrownBy(() -> service.update(null, request))
                .isInstanceOf(SGCCInvalidRequestException.class)
                .hasMessageContaining("inválidos");
        }

        @Test
        @DisplayName("Deve lançar exceção quando request é nulo")
        void shouldThrowWhenRequestIsNull() {
            // When / Then
            assertThatThrownBy(() -> service.update(1L, null))
                .isInstanceOf(SGCCInvalidRequestException.class)
                .hasMessageContaining("inválidos");
        }

        @Test
        @DisplayName("Deve lançar exceção quando localidade não encontrada")
        void shouldThrowWhenLocaleNotFound() {
            // Given
            var request = new OpsLocaleRequestDto("BRASÍLIA");
            var localeToUpdate = new OpsLocale("BRASÍLIA");
            when(mapper.toEntity(request)).thenReturn(localeToUpdate);
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.update(999L, request))
                .isInstanceOf(SGCCResourceNotFoundException.class)
                .hasMessageContaining("Recurso não encontrado");

            verify(mapper).toEntity(request);
            verify(repository).findById(999L);
        }
    }

    @Nested
    @DisplayName("UseCase: Deletar localidade")
    class Delete {

        @Test
        @DisplayName("Deve deletar localidade com sucesso")
        void shouldDeleteLocaleSuccessfully() {
            // Given
            var entity = localeWithId("ICEA", 1L);
            when(repository.findById(1L)).thenReturn(Optional.of(entity));

            // When
            service.delete(1L);

            // Then
            verify(repository).findById(1L);
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID é nulo")
        void shouldThrowWhenIdIsNull() {
            // When / Then
            assertThatThrownBy(() -> service.delete(null))
                .isInstanceOf(SGCCInvalidRequestException.class)
                .hasMessageContaining("deve estar presente");
        }

        @Test
        @DisplayName("Deve lançar exceção quando localidade não encontrada")
        void shouldThrowWhenLocaleNotFound() {
            // Given
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(SGCCResourceNotFoundException.class)
                .hasMessageContaining("Recurso não encontrado");

            verify(repository).findById(999L);
        }
    }
}
