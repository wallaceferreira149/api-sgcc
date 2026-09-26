package dev.arcanus.api_sgcc.modules.ops_logs.mappers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.AircraftType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Mapper de tipos de aeronave")
class AircraftTypeMapperTest {

    private final AircraftTypeMapper mapper = new AircraftTypeMapper();

    @ParameterizedTest
    @DisplayName("Deve converter o DTO de requisição em entidade normalizando o código")
    @CsvSource({
        "a-29,  A-29",
        "c-105, C-105",
        "f-5,   F-5"
    })
    void shouldConvertRequestToEntity(String code, String normalizedCode) {
        // Given
        AircraftTypeRequestDto request = new AircraftTypeRequestDto(code);

        // When
        AircraftType entity = mapper.toEntity(request);

        // Then
        assertEquals(normalizedCode, entity.getCode());
    }

    @Test
    @DisplayName("Deve remover espaços em branco do código na conversão")
    void shouldTrimCodeOnConversion() {
        // Given
        AircraftTypeRequestDto request = new AircraftTypeRequestDto("  a-29  ");

        // When
        AircraftType entity = mapper.toEntity(request);

        // Then
        assertEquals("A-29", entity.getCode());
    }

    @Test
    @DisplayName("Deve normalizar o código independentemente do locale padrão do sistema")
    void shouldNormalizeCodeIndependentlyOfDefaultLocale() {
        // Given
        Locale defaultLocale = Locale.getDefault();

        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            // When
            AircraftType entity = mapper.toEntity(new AircraftTypeRequestDto("i-16"));

            // Then
            assertEquals("I-16", entity.getCode());
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @ParameterizedTest
    @DisplayName("Deve rejeitar código nulo ou em branco na conversão")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidCodeOnConversion(String invalidCode) {
        // Given
        AircraftTypeRequestDto request = new AircraftTypeRequestDto(invalidCode);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(request));
    }

    @Test
    @DisplayName("Deve converter a entidade em DTO de resposta")
    void shouldConvertEntityToResponse() {
        // Given
        AircraftType persisted = new AircraftType("a-29");
        // O id é atribuído pelo banco (IDENTITY); aqui a entidade ainda não foi persistida

        // When
        AircraftTypeResponseDto response = mapper.toResponse(persisted);

        // Then
        assertAll(
            () -> assertNull(response.id()),
            () -> assertEquals("A-29", response.code())
        );
    }
}
