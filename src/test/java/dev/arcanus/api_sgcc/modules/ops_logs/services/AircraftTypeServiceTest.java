package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCInvalidRequestException;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.AircraftType;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.AircraftTypeMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.AircraftTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Serviço de tipos de aeronave")
class AircraftTypeServiceTest {

    private static final String ALREADY_EXISTS_MESSAGE =
        "O tipo de aeronave já existe. Utilize o código canônico no formato A-29.";

    @Mock
    private AircraftTypeRepository repository;

    @Mock
    private AircraftTypeMapper mapper;

    @InjectMocks
    private AircraftTypeService service;

    @Nested
    @DisplayName("Caso de uso: criar tipo de aeronave")
    class CreateAircraftTypeUseCase {

        private final AircraftTypeRequestDto request = new AircraftTypeRequestDto("a-29");

        @Test
        @DisplayName("Deve criar um tipo de aeronave com sucesso")
        void shouldCreateAircraftTypeSuccessfully() {
            // Given
            AircraftType entity = new AircraftType("a-29");
            AircraftTypeResponseDto expectedResponse = new AircraftTypeResponseDto(1L, "A-29");

            when(repository.existsByCodeKey("A29")).thenReturn(false);
            when(mapper.toEntity(request)).thenReturn(entity);
            when(repository.save(entity)).thenAnswer(invocation -> invocation.getArgument(0));
            when(mapper.toResponse(entity)).thenReturn(expectedResponse);

            // When
            AircraftTypeResponseDto created = service.create(request);

            // Then
            assertAll(
                () -> assertEquals(expectedResponse.id(), created.id()),
                () -> assertEquals(expectedResponse.code(), created.code())
            );
            verify(repository).existsByCodeKey("A29");
            verify(repository).save(entity);
        }

        @ParameterizedTest
        @DisplayName("Deve normalizar o código para maiúsculo antes de verificar duplicidade")
        @CsvSource({
            "a-29,  A-29, A29",
            "c-105, C-105, C105",
            "f-5,   F-5,  F5"
        })
        void shouldNormalizeCodeBeforeCheckingDuplicates(
                String code, String normalizedCode, String codeKey) {
            // Given
            AircraftTypeRequestDto parameterizedRequest = new AircraftTypeRequestDto(code);
            AircraftType entity = new AircraftType(code);

            when(repository.existsByCodeKey(codeKey)).thenReturn(false);
            when(mapper.toEntity(parameterizedRequest)).thenReturn(entity);
            when(repository.save(entity)).thenAnswer(invocation -> invocation.getArgument(0));
            when(mapper.toResponse(entity)).thenReturn(new AircraftTypeResponseDto(1L, normalizedCode));

            // When
            AircraftTypeResponseDto created = service.create(parameterizedRequest);

            // Then
            verify(repository).existsByCodeKey(codeKey);
            assertEquals(normalizedCode, created.code());
        }

        @Test
        @DisplayName("Deve remover espaços em branco do código antes de verificar duplicidade")
        void shouldTrimCodeBeforeCheckingDuplicates() {
            // Given
            AircraftTypeRequestDto requestWithSpaces = new AircraftTypeRequestDto("  a-29  ");
            AircraftType entity = new AircraftType("a-29");

            when(repository.existsByCodeKey("A29")).thenReturn(false);
            when(mapper.toEntity(requestWithSpaces)).thenReturn(entity);
            when(repository.save(entity)).thenAnswer(invocation -> invocation.getArgument(0));
            when(mapper.toResponse(entity)).thenReturn(new AircraftTypeResponseDto(1L, "A-29"));

            // When
            service.create(requestWithSpaces);

            // Then
            verify(repository).existsByCodeKey("A29");
        }

        @Test
        @DisplayName("Não deve criar um tipo de aeronave que já existe")
        void shouldNotCreateAircraftTypeAlreadyExists() {
            // Given
            when(repository.existsByCodeKey("A29")).thenReturn(true);

            // When / Then
            SGCCResourceAlreadyExists exception = assertThrows(
                SGCCResourceAlreadyExists.class,
                () -> service.create(request)
            );

            assertAll(
                () -> assertEquals(ALREADY_EXISTS_MESSAGE, exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
            );
            verify(repository, never()).save(any(AircraftType.class));
            verifyNoInteractions(mapper);
        }

        @ParameterizedTest
        @DisplayName("Não deve criar um tipo de aeronave já cadastrado com outra grafia do código")
        @CsvSource({
            "A29,   A-29",
            "a29,   A-29",
            "'A 29', A-29",
            "KC390, KC-390",
            "rq1150, RQ-1150"
        })
        void shouldNotCreateAircraftTypeWhenCodeIsEquivalentIgnoringHyphen(
                String equivalentCode, String existingCode) {
            // Given
            AircraftTypeRequestDto equivalentRequest = new AircraftTypeRequestDto(equivalentCode);
            AircraftType existingAircraftType = new AircraftType(existingCode);
            String codeKey = existingCode.replace("-", "");

            when(repository.existsByCodeKey(codeKey)).thenReturn(true);

            // When / Then
            SGCCResourceAlreadyExists exception = assertThrows(
                SGCCResourceAlreadyExists.class,
                () -> service.create(equivalentRequest)
            );

            assertAll(
                () -> assertEquals(ALREADY_EXISTS_MESSAGE, exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
            );
            verify(repository, never()).save(any(AircraftType.class));
            verifyNoInteractions(mapper);
            assertEquals(existingCode, existingAircraftType.getCode());
        }

        @ParameterizedTest
        @DisplayName("Não deve criar um tipo de aeronave com código nulo ou em branco")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void shouldNotCreateAircraftTypeWithInvalidCode(String invalidCode) {
            // Given
            AircraftTypeRequestDto invalidRequest = new AircraftTypeRequestDto(invalidCode);

            // When / Then
            SGCCInvalidRequestException exception = assertThrows(
                SGCCInvalidRequestException.class,
                () -> service.create(invalidRequest)
            );

            assertAll(
                () -> assertEquals("O tipo de aeronave não pode ser vazio.", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
            );
            verifyNoInteractions(repository, mapper);
        }
    }

    @Nested
    @DisplayName("Caso de uso: buscar tipos de aeronave")
    class FindAircraftTypeUseCase {

        @Test
        @DisplayName("Deve listar todos os tipos de aeronave")
        void shouldListAllAircraftTypes() {
            // Given
            AircraftType a29 = new AircraftType("a-29");
            AircraftType f5 = new AircraftType("f-5");

            when(repository.findAll()).thenReturn(List.of(a29, f5));
            when(mapper.toResponse(a29)).thenReturn(new AircraftTypeResponseDto(1L, "A-29"));
            when(mapper.toResponse(f5)).thenReturn(new AircraftTypeResponseDto(2L, "F-5"));

            // When
            List<AircraftTypeResponseDto> aircraftTypes = service.findAll();

            // Then
            assertAll(
                () -> assertEquals(2, aircraftTypes.size()),
                () -> assertEquals(1L, aircraftTypes.getFirst().id()),
                () -> assertEquals("A-29", aircraftTypes.getFirst().code()),
                () -> assertEquals(2L, aircraftTypes.get(1).id()),
                () -> assertEquals("F-5", aircraftTypes.get(1).code())
            );
            verify(repository).findAll();
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando não existirem tipos de aeronave")
        void shouldReturnEmptyListWhenNoAircraftTypesExist() {
            // Given
            when(repository.findAll()).thenReturn(List.of());

            // When
            List<AircraftTypeResponseDto> aircraftTypes = service.findAll();

            // Then
            assertTrue(aircraftTypes.isEmpty());
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Deve encontrar um tipo de aeronave pelo Id")
        void shouldFindAircraftTypeById() {
            // Given
            AircraftType a29 = new AircraftType("a-29");
            AircraftTypeResponseDto expectedResponse = new AircraftTypeResponseDto(1L, "A-29");

            when(repository.findById(1L)).thenReturn(Optional.of(a29));
            when(mapper.toResponse(a29)).thenReturn(expectedResponse);

            // When
            AircraftTypeResponseDto aircraftType = service.findById(1L);

            // Then
            assertAll(
                () -> assertEquals(expectedResponse.id(), aircraftType.id()),
                () -> assertEquals(expectedResponse.code(), aircraftType.code())
            );
            verify(repository).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o tipo de aeronave não for encontrado na busca por Id")
        void shouldThrowWhenAircraftTypeNotFoundById() {
            // Given
            when(repository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            SGCCResourceNotFoundException exception = assertThrows(
                SGCCResourceNotFoundException.class,
                () -> service.findById(99L)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            verifyNoInteractions(mapper);
        }
    }

    @Nested
    @DisplayName("Caso de uso: atualizar tipo de aeronave")
    class UpdateAircraftTypeUseCase {

        private final AircraftTypeRequestDto request = new AircraftTypeRequestDto("c-105");
        private final AircraftTypeResponseDto expectedResponse = new AircraftTypeResponseDto(1L, "C-105");

        @Test
        @DisplayName("Deve atualizar um tipo de aeronave com sucesso")
        void shouldUpdateAircraftTypeSuccessfully() {
            // Given
            AircraftType existingAircraftType = new AircraftType("a-29");

            when(repository.findById(1L)).thenReturn(Optional.of(existingAircraftType));
            when(repository.existsByCodeKeyAndIdNot("C105", 1L)).thenReturn(false);
            when(repository.save(existingAircraftType))
                .thenAnswer(invocation -> invocation.getArgument(0));
            when(mapper.toResponse(existingAircraftType)).thenReturn(expectedResponse);

            // When
            AircraftTypeResponseDto updated = service.update(1L, request);

            // Then
            assertAll(
                () -> assertEquals(expectedResponse.id(), updated.id()),
                () -> assertEquals(expectedResponse.code(), updated.code()),
                () -> assertEquals("C-105", existingAircraftType.getCode())
            );
            verify(repository).findById(1L);
            verify(repository).existsByCodeKeyAndIdNot("C105", 1L);
            verify(repository).save(existingAircraftType);
        }

        @Test
        @DisplayName("Deve atualizar um tipo de aeronave mantendo o próprio código")
        void shouldUpdateAircraftTypeWithoutChangingItsOwnCode() {
            // Given
            AircraftType existingAircraftType = new AircraftType("a-29");
            AircraftTypeRequestDto sameCodeRequest = new AircraftTypeRequestDto("  a-29  ");

            when(repository.findById(1L)).thenReturn(Optional.of(existingAircraftType));
            when(repository.existsByCodeKeyAndIdNot("A29", 1L)).thenReturn(false);
            when(repository.save(existingAircraftType))
                .thenAnswer(invocation -> invocation.getArgument(0));
            when(mapper.toResponse(existingAircraftType))
                .thenReturn(new AircraftTypeResponseDto(1L, "A-29"));

            // When
            AircraftTypeResponseDto updated = service.update(1L, sameCodeRequest);

            // Then
            assertAll(
                () -> assertEquals("A-29", updated.code()),
                () -> assertEquals("A-29", existingAircraftType.getCode())
            );
            verify(repository).save(existingAircraftType);
        }

        @ParameterizedTest
        @DisplayName("Não deve atualizar para o código de outro tipo de aeronave, com ou sem hífen")
        @CsvSource({
            "C105,  C-105",
            "c105,  C-105",
            "c-105, C-105",
            "'C 105', C-105"
        })
        void shouldNotUpdateAircraftTypeToAnotherAircraftTypeCode(
                String requestedCode, String codeOfAnotherRecord) {
            // Given
            AircraftType existingAircraftType = new AircraftType("a-29");
            AircraftTypeRequestDto requestWithOtherCode = new AircraftTypeRequestDto(requestedCode);
            String codeKey = codeOfAnotherRecord.replace("-", "");

            when(repository.findById(1L)).thenReturn(Optional.of(existingAircraftType));
            when(repository.existsByCodeKeyAndIdNot(codeKey, 1L)).thenReturn(true);

            // When / Then
            SGCCResourceAlreadyExists exception = assertThrows(
                SGCCResourceAlreadyExists.class,
                () -> service.update(1L, requestWithOtherCode)
            );

            assertAll(
                () -> assertEquals(ALREADY_EXISTS_MESSAGE, exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus()),
                () -> assertEquals("A-29", existingAircraftType.getCode())
            );
            verify(repository, never()).save(any(AircraftType.class));
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Não deve atualizar um tipo de aeronave com Id inexistente")
        void shouldNotUpdateAircraftTypeWhenIdNotFound() {
            // Given
            when(repository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            SGCCResourceNotFoundException exception = assertThrows(
                SGCCResourceNotFoundException.class,
                () -> service.update(99L, request)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            verify(repository, never()).save(any(AircraftType.class));
            verifyNoInteractions(mapper);
        }

        @ParameterizedTest
        @DisplayName("Não deve atualizar um tipo de aeronave com código nulo ou em branco")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void shouldNotUpdateAircraftTypeWithInvalidCode(String invalidCode) {
            // Given
            AircraftTypeRequestDto invalidRequest = new AircraftTypeRequestDto(invalidCode);
            when(repository.findById(1L)).thenReturn(Optional.of(new AircraftType("a-29")));

            // When / Then
            SGCCInvalidRequestException exception = assertThrows(
                SGCCInvalidRequestException.class,
                () -> service.update(1L, invalidRequest)
            );

            assertAll(
                () -> assertEquals("O tipo de aeronave não pode ser vazio.", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
            );
            verify(repository, never()).save(any(AircraftType.class));
        }
    }

    @Nested
    @DisplayName("Caso de uso: excluir tipo de aeronave")
    class DeleteAircraftTypeUseCase {

        @Test
        @DisplayName("Deve excluir um tipo de aeronave com sucesso")
        void shouldDeleteAircraftTypeSuccessfully() {
            // Given
            when(repository.findById(1L)).thenReturn(Optional.of(new AircraftType("a-29")));

            // When
            service.delete(1L);

            // Then
            verify(repository).findById(1L);
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("Não deve excluir um tipo de aeronave com Id inexistente")
        void shouldNotDeleteAircraftTypeWhenIdNotFound() {
            // Given
            when(repository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            SGCCResourceNotFoundException exception = assertThrows(
                SGCCResourceNotFoundException.class,
                () -> service.delete(99L)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            verify(repository, never()).deleteById(anyLong());
        }
    }
}
