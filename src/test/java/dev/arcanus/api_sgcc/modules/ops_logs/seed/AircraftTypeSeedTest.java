package dev.arcanus.api_sgcc.modules.ops_logs.seed;

import dev.arcanus.api_sgcc.modules.ops_logs.entities.AircraftType;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.AircraftTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * O seed roda no boot do perfil "dev" (spring.sql.init), entao so faz sentido
 * validar com o contexto completo da aplicacao.
 */
@SpringBootTest
@ActiveProfiles("dev")
@DisplayName("Seed de tipos de aeronave do perfil dev")
class AircraftTypeSeedTest {

    private static final List<String> EXPECTED_CODES = List.of(
        "A-1", "A-29", "F-5", "F-39", "R-35", "R-99", "E-99", "C-95", "C-97", "C-98",
        "C-105", "C-130", "KC-390", "KC-30", "VC-1", "VC-2", "VC-99", "H-36", "H-50",
        "H-60", "AH-2", "T-25", "T-27", "P-3", "P-95", "SC-105", "RQ-450", "RQ-900",
        "RQ-1150"
    );

    @Autowired
    private AircraftTypeRepository repository;

    @Test
    @DisplayName("Deve carregar o catálogo de tipos de aeronave da FAB")
    void shouldLoadFabAircraftTypeCatalog() {
        // When
        List<String> codes = repository.findAll().stream()
            .map(AircraftType::getCode)
            .sorted()
            .toList();

        // Then
        assertAll(
            () -> assertEquals(EXPECTED_CODES.size(), codes.size(),
                "O seed deveria gravar exatamente " + EXPECTED_CODES.size() + " tipos de aeronave"),
            () -> assertTrue(codes.containsAll(EXPECTED_CODES),
                "Faltaram tipos de aeronave do catálogo: "
                    + EXPECTED_CODES.stream().filter(code -> !codes.contains(code)).toList())
        );
    }

    @Test
    @DisplayName("Deve bloquear o cadastro de uma aeronave já presente com outra grafia")
    void shouldDetectSeededAircraftTypeWithoutHyphen() {
        // When / Then
        assertAll(
            () -> assertTrue(repository.existsByCodeKey("A29"), "A-29 sem hífen deve colidir"),
            () -> assertTrue(repository.existsByCodeKey("KC390"), "KC-390 sem hífen deve colidir"),
            () -> assertTrue(repository.existsByCodeKey("RQ1150"), "RQ-1150 sem hífen deve colidir")
        );
    }
}
