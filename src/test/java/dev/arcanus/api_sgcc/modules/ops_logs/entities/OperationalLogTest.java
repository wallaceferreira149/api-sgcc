package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.modules.ops_logs.value_objects.FlightControl;
import dev.arcanus.api_sgcc.modules.ops_logs.value_objects.FlightInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationalLogTest {

    private static final String CREW_ERROR =
            "Um usuário não pode desempenhar mais de uma função para uma mesma manutenção operacional";
    private static final String FLIGHT_CONTROL_REQUIRED_ERROR =
            "O controle de voo deve ser definido para operações reais.";
    private static final String FLIGHT_CONTROL_NOT_ALLOWED_ERROR =
            "O controle de voo só pode ser definido para operações reais.";
    private static final String QUANTITY_ERROR =
            "A quantidade de aeronaves deve ser positiva.";
    private static final String CREW_PARAMETER_ERROR =
            "Um dos parâmetros deve ser preenchido.";

    private static final Instant OPSLOG_DATE = Instant.parse("2026-09-24T12:00:00Z");

    private OpsRole opsRole;
    private OpsUser operator;
    private OpsUser assistant;
    private OpsUser instructor;
    private OpsLocale locale;
    private InstructionOrder instructionOrder;
    private FlightControl flightControl;

    @BeforeEach
    void setUp() {
        opsRole = new OpsRole("CC", 120L);
        operator = new OpsUser("operator@fab.mil.br", opsRole);
        assistant = new OpsUser("assistant@fab.mil.br", opsRole);
        instructor = new OpsUser("instructor@fab.mil.br", opsRole);
        locale = new OpsLocale("ICEA");
        instructionOrder = new InstructionOrder("OI-001", 1, "Ordem de instrução", opsRole);
        flightControl = createFlightControl();
    }

    @Test
    @DisplayName("Deve criar um registro operacional real com todos os dados")
    void shouldCreateRealOperationalLogWithAllValues() {
        OperationalLog operationalLog = validRealBuilder()
                .assistant(assistant)
                .instructor(instructor)
                .build();

        assertAll(
                () -> assertSame(operator, operationalLog.getOperator()),
                () -> assertSame(assistant, operationalLog.getAssistant()),
                () -> assertSame(instructor, operationalLog.getInstructor()),
                () -> assertSame(locale, operationalLog.getLocale()),
                () -> assertSame(instructionOrder, operationalLog.getInstructionOrder()),
                () -> assertSame(opsRole, operationalLog.getOpsRole()),
                () -> assertEquals(OPSLOG_DATE, operationalLog.getOpslogDate()),
                () -> assertTrue(operationalLog.isReal()),
                () -> assertEquals(2, operationalLog.getQuantityRegistered()),
                () -> assertSame(flightControl, operationalLog.getFlightControl()),
                () -> assertEquals(
                        List.of(operator, assistant, instructor),
                        operationalLog.getCrew()
                )
        );
    }

    @Test
    @DisplayName("Deve criar um registro operacional simulado sem integrantes opcionais")
    void shouldCreateSimulatedOperationalLogWithoutOptionalValues() {
        OperationalLog operationalLog = validSimulatedBuilder().build();

        assertAll(
                () -> assertSame(operator, operationalLog.getOperator()),
                () -> assertNull(operationalLog.getAssistant()),
                () -> assertNull(operationalLog.getInstructor()),
                () -> assertNull(operationalLog.getFlightControl()),
                () -> assertFalse(operationalLog.isReal()),
                () -> assertEquals(1, operationalLog.getQuantityRegistered()),
                () -> assertEquals(List.of(operator), operationalLog.getCrew())
        );
    }

    @Test
    @DisplayName("Deve permitir um assistente sem instrutor")
    void shouldCreateOperationalLogWithOnlyAssistant() {
        OperationalLog operationalLog = validSimulatedBuilder()
                .assistant(assistant)
                .build();

        assertAll(
                () -> assertSame(assistant, operationalLog.getAssistant()),
                () -> assertNull(operationalLog.getInstructor()),
                () -> assertEquals(List.of(operator, assistant), operationalLog.getCrew())
        );
    }

    @Test
    @DisplayName("Deve permitir um instrutor sem assistente")
    void shouldCreateOperationalLogWithOnlyInstructor() {
        OperationalLog operationalLog = validSimulatedBuilder()
                .instructor(instructor)
                .build();

        assertAll(
                () -> assertNull(operationalLog.getAssistant()),
                () -> assertSame(instructor, operationalLog.getInstructor()),
                () -> assertEquals(List.of(operator, instructor), operationalLog.getCrew())
        );
    }

    @Nested
    @DisplayName("Validação dos campos obrigatórios")
    class RequiredFieldsValidation {

        @Test
        @DisplayName("Não deve aceitar operador nulo")
        void shouldRejectNullOperator() {
            assertInvalid(
                    () -> new OperationalLog.Builder().operator(null),
                    "O operador não pode ser nulo."
            );
        }

        @Test
        @DisplayName("Não deve aceitar localidade nula")
        void shouldRejectNullLocale() {
            assertInvalid(
                    () -> new OperationalLog.Builder().locale(null),
                    "A localidade é obrigatória."
            );
        }

        @Test
        @DisplayName("Não deve aceitar ordem de instrução nula")
        void shouldRejectNullInstructionOrder() {
            assertInvalid(
                    () -> new OperationalLog.Builder().instructionOrder(null),
                    "A ordem de instrução não pode ser nulo."
            );
        }

        @Test
        @DisplayName("Não deve aceitar papel operacional nulo")
        void shouldRejectNullOpsRole() {
            assertInvalid(
                    () -> new OperationalLog.Builder().opsRole(null),
                    "O papel do operador não pode ser nulo."
            );
        }

        @Test
        @DisplayName("Não deve aceitar data nula")
        void shouldRejectNullOpslogDate() {
            assertInvalid(
                    () -> new OperationalLog.Builder().opslogDate(null),
                    "A data do registro operacional não pode ser nulo."
            );
        }
    }

    @Nested
    @DisplayName("Validação da tripulação")
    class CrewValidation {

        @Test
        @DisplayName("Não deve permitir o operador como assistente")
        void shouldRejectOperatorAsAssistant() {
            assertInvalid(
                    () -> requiredBuilder().assistant(operator).build(),
                    CREW_ERROR
            );
        }

        @Test
        @DisplayName("Não deve permitir outro usuário com o mesmo e-mail do operador como instrutor")
        void shouldRejectEquivalentOperatorAsInstructor() {
            OpsUser equivalentOperator = new OpsUser(operator.getEmail(), opsRole);

            assertInvalid(
                    () -> requiredBuilder().instructor(equivalentOperator).build(),
                    CREW_ERROR
            );
        }

        @Test
        @DisplayName("Não deve permitir o mesmo assistente e instrutor")
        void shouldRejectSameAssistantAndInstructor() {
            assertInvalid(
                    () -> requiredBuilder()
                            .assistant(assistant)
                            .instructor(assistant)
                            .build(),
                    CREW_ERROR
            );
        }
    }

    @Nested
    @DisplayName("Validação da quantidade")
    class QuantityValidation {

        @Test
        @DisplayName("Não deve aceitar quantidade zero")
        void shouldRejectZeroQuantity() {
            assertInvalid(
                    () -> requiredBuilder().quantityRegistered(0).build(),
                    QUANTITY_ERROR
            );
        }

        @Test
        @DisplayName("Não deve aceitar quantidade negativa")
        void shouldRejectNegativeQuantity() {
            assertInvalid(
                    () -> requiredBuilder().quantityRegistered(-1).build(),
                    QUANTITY_ERROR
            );
        }

        @Test
        @DisplayName("Deve aceitar a menor quantidade positiva")
        void shouldAcceptMinimumPositiveQuantity() {
            assertDoesNotThrow(() -> requiredBuilder().quantityRegistered(1).build());
        }
    }

    @Nested
    @DisplayName("Validação do controle de voo")
    class FlightControlValidation {

        @Test
        @DisplayName("Não deve permitir operação real sem controle de voo")
        void shouldRejectRealOperationWithoutFlightControl() {
            assertInvalid(
                    () -> requiredBuilder().isReal(true).build(),
                    FLIGHT_CONTROL_REQUIRED_ERROR
            );
        }

        @Test
        @DisplayName("Não deve permitir controle de voo em operação simulada")
        void shouldRejectFlightControlForSimulatedOperation() {
            assertInvalid(
                    () -> requiredBuilder()
                            .isReal(false)
                            .flightControl(flightControl)
                            .build(),
                    FLIGHT_CONTROL_NOT_ALLOWED_ERROR
            );
        }
    }

    @Nested
    @DisplayName("Reatribuição da tripulação")
    class CrewReassignment {

        @Test
        @DisplayName("Deve reatribuir assistente e instrutor simultaneamente")
        void shouldReassignAssistantAndInstructor() {
            OperationalLog operationalLog = validSimulatedBuilder()
                    .assistant(assistant)
                    .build();
            OpsUser newAssistant = new OpsUser("new-assistant@fab.mil.br", opsRole);
            OpsUser newInstructor = new OpsUser("new-instructor@fab.mil.br", opsRole);

            operationalLog.reassignCrew(newAssistant, newInstructor);

            assertAll(
                    () -> assertSame(newAssistant, operationalLog.getAssistant()),
                    () -> assertSame(newInstructor, operationalLog.getInstructor()),
                    () -> assertEquals(
                            List.of(operator, newAssistant, newInstructor),
                            operationalLog.getCrew()
                    )
            );
        }

        @Test
        @DisplayName("Deve reatribuir somente o assistente")
        void shouldReassignOnlyAssistant() {
            OperationalLog operationalLog = validSimulatedBuilder()
                    .instructor(instructor)
                    .build();

            operationalLog.reassignCrew(assistant, null);

            assertAll(
                    () -> assertSame(assistant, operationalLog.getAssistant()),
                    () -> assertNull(operationalLog.getInstructor()),
                    () -> assertEquals(List.of(operator, assistant), operationalLog.getCrew())
            );
        }

        @Test
        @DisplayName("Deve reatribuir somente o instrutor")
        void shouldReassignOnlyInstructor() {
            OperationalLog operationalLog = validSimulatedBuilder()
                    .assistant(assistant)
                    .build();

            operationalLog.reassignCrew(null, instructor);

            assertAll(
                    () -> assertNull(operationalLog.getAssistant()),
                    () -> assertSame(instructor, operationalLog.getInstructor()),
                    () -> assertEquals(List.of(operator, instructor), operationalLog.getCrew())
            );
        }

        @Test
        @DisplayName("Não deve reatribuir assistente e instrutor nulos")
        void shouldRejectNullAssistantAndInstructor() {
            OperationalLog operationalLog = validSimulatedBuilder()
                    .assistant(assistant)
                    .instructor(instructor)
                    .build();

            assertInvalid(
                    () -> operationalLog.reassignCrew(null, null),
                    CREW_PARAMETER_ERROR
            );

            assertAll(
                    () -> assertSame(assistant, operationalLog.getAssistant()),
                    () -> assertSame(instructor, operationalLog.getInstructor())
            );
        }

        @Test
        @DisplayName("Não deve reatribuir o operador como assistente")
        void shouldRejectOperatorAsAssistantDuringReassignment() {
            OperationalLog operationalLog = validSimulatedBuilder()
                    .assistant(assistant)
                    .build();

            assertInvalid(
                    () -> operationalLog.reassignCrew(operator, null),
                    CREW_ERROR
            );

            assertAll(
                    () -> assertSame(assistant, operationalLog.getAssistant()),
                    () -> assertNull(operationalLog.getInstructor())
            );
        }

        @Test
        @DisplayName("Não deve reatribuir o operador como instrutor")
        void shouldRejectOperatorAsInstructorDuringReassignment() {
            OperationalLog operationalLog = validSimulatedBuilder()
                    .instructor(instructor)
                    .build();

            assertInvalid(
                    () -> operationalLog.reassignCrew(null, operator),
                    CREW_ERROR
            );

            assertAll(
                    () -> assertNull(operationalLog.getAssistant()),
                    () -> assertSame(instructor, operationalLog.getInstructor())
            );
        }

        @Test
        @DisplayName("Não deve reatribuir o mesmo usuário como assistente e instrutor")
        void shouldRejectSameAssistantAndInstructorDuringReassignment() {
            OperationalLog operationalLog = validSimulatedBuilder().build();

            assertInvalid(
                    () -> operationalLog.reassignCrew(assistant, assistant),
                    CREW_ERROR
            );

            assertAll(
                    () -> assertNull(operationalLog.getAssistant()),
                    () -> assertNull(operationalLog.getInstructor())
            );
        }
    }

    @Nested
    @DisplayName("Igualdade da entidade")
    class Equality {

        @Test
        @DisplayName("Deve ser igual à própria instância")
        void shouldBeEqualToItself() {
            OperationalLog operationalLog = validSimulatedBuilder().build();

            assertEquals(operationalLog, operationalLog);
        }

        @Test
        @DisplayName("Não deve ser igual a outra entidade transiente")
        void shouldNotEqualAnotherTransientEntity() {
            OperationalLog first = validSimulatedBuilder().build();
            OperationalLog second = validSimulatedBuilder().build();

            assertNotEquals(first, second);
        }

        @Test
        @DisplayName("Deve comparar entidades persistidas pelo identificador")
        void shouldComparePersistedEntitiesById() {
            OperationalLog first = validSimulatedBuilder().build();
            OperationalLog second = validSimulatedBuilder().build();
            ReflectionTestUtils.setField(first, "id", 10L);
            ReflectionTestUtils.setField(second, "id", 10L);

            assertAll(
                    () -> assertEquals(first, second),
                    () -> assertEquals(first.hashCode(), second.hashCode())
            );
        }

        @Test
        @DisplayName("Não deve comparar entidades persistidas com identificadores diferentes")
        void shouldNotEqualPersistedEntitiesWithDifferentIds() {
            OperationalLog first = validSimulatedBuilder().build();
            OperationalLog second = validSimulatedBuilder().build();
            ReflectionTestUtils.setField(first, "id", 10L);
            ReflectionTestUtils.setField(second, "id", 20L);

            assertNotEquals(first, second);
        }
    }

    private OperationalLog.Builder requiredBuilder() {
        return new OperationalLog.Builder()
                .operator(operator)
                .locale(locale)
                .instructionOrder(instructionOrder)
                .opsRole(opsRole)
                .opslogDate(OPSLOG_DATE)
                .quantityRegistered(1);
    }

    private OperationalLog.Builder validRealBuilder() {
        return requiredBuilder()
                .isReal(true)
                .quantityRegistered(2)
                .flightControl(flightControl);
    }

    private OperationalLog.Builder validSimulatedBuilder() {
        return requiredBuilder()
                .isReal(false)
                .quantityRegistered(1);
    }

    private FlightControl createFlightControl() {
        FlightInfo flightInfo = FlightInfo.of(
                "FAB-01",
                new AircraftType("F-5"),
                1,
                "1234"
        );

        return FlightControl.of(
                Instant.parse("2026-09-24T10:00:00Z"),
                Instant.parse("2026-09-24T11:00:00Z"),
                flightInfo
        );
    }

    private static void assertInvalid(Executable action, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                action
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
