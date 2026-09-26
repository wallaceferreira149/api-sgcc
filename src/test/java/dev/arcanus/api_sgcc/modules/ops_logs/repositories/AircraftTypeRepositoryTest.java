package dev.arcanus.api_sgcc.modules.ops_logs.repositories;

import dev.arcanus.api_sgcc.modules.ops_logs.entities.AircraftType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A comparacao de codigo acontece em JPQL (REPLACE), entao so pode ser validada
 * contra um banco de verdade: mock do repository nao provaria nada aqui.
 */
@DataJpaTest(properties = "spring.flyway.enabled=false")
@ActiveProfiles("test")
@DisplayName("Repositório de tipos de aeronave")
class AircraftTypeRepositoryTest {

    @Autowired
    private AircraftTypeRepository repository;

    @Nested
    @DisplayName("existsByCodeKey")
    class ExistsByCodeKey {

        @ParameterizedTest(name = "A-29 e \"{0}\" são o mesmo tipo de aeronave")
        @ValueSource(strings = {"A29", "a29", "A 29", "a 29", "A-29", " a-29 "})
        @DisplayName("Deve encontrar o mesmo tipo de aeronave com ou sem hífen e caixa")
        void shouldFindSameAircraftTypeRegardlessOfHyphenAndCase(String code) {
            // Given
            repository.save(new AircraftType("A-29"));

            // When / Then
            assertTrue(repository.existsByCodeKey(code));
        }

        @Test
        @DisplayName("Deve encontrar outros tipos de aeronave do catálogo")
        void shouldFindOtherAircraftTypes() {
            // Given
            repository.save(new AircraftType("KC-390"));
            repository.save(new AircraftType("RQ-1150"));
            repository.save(new AircraftType("C-105"));

            // When / Then
            assertTrue(repository.existsByCodeKey("KC390"));
            assertTrue(repository.existsByCodeKey("RQ1150"));
            assertTrue(repository.existsByCodeKey("C105"));
        }

        @ParameterizedTest(name = "\"{0}\" não pode colidir com A-29")
        @ValueSource(strings = {"A2", "A292", "A-290", "F5", "KC39", "A-29X"})
        @DisplayName("Não deve encontrar um tipo de aeronave diferente")
        void shouldNotFindDifferentAircraftType(String codeKey) {
            // Given
            repository.save(new AircraftType("A-29"));

            // When / Then
            assertFalse(repository.existsByCodeKey(codeKey));
        }

        @Test
        @DisplayName("Não deve encontrar nada quando o catálogo estiver vazio")
        void shouldNotFindWhenCatalogIsEmpty() {
            // When / Then
            assertFalse(repository.existsByCodeKey("A29"));
        }
    }

    @Nested
    @DisplayName("existsByCodeKeyAndIdNot")
    class ExistsByCodeKeyAndIdNot {

        @Test
        @DisplayName("Não deve encontrar o próprio registro na comparação de código")
        void shouldIgnoreSameRecord() {
            // Given
            AircraftType persisted = repository.save(new AircraftType("A-29"));

            // When / Then
            assertFalse(repository.existsByCodeKeyAndIdNot("A29", persisted.getId()));
        }

        @Test
        @DisplayName("Deve encontrar o código de outro registro ignorando o hífen")
        void shouldFindOtherRecordIgnoringHyphen() {
            // Given
            AircraftType a29 = repository.save(new AircraftType("A-29"));
            repository.save(new AircraftType("C-105"));

            // When / Then
            assertTrue(repository.existsByCodeKeyAndIdNot("C105", a29.getId()));
            assertTrue(repository.existsByCodeKeyAndIdNot("c105", a29.getId()));
        }

        @Test
        @DisplayName("Deve ignorar só o próprio registro e continuar achando os demais")
        void shouldIgnoreOnlySameRecord() {
            // Given
            AircraftType kc390 = repository.save(new AircraftType("KC-390"));
            repository.save(new AircraftType("RQ-1150"));

            // When / Then
            assertFalse(repository.existsByCodeKeyAndIdNot("KC390", kc390.getId()));
            assertTrue(repository.existsByCodeKeyAndIdNot("RQ1150", kc390.getId()));
        }
    }
}
