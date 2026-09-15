package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstructionOrderTest {

    private OpsRole validRole;

    @BeforeEach
    void setUp() {
        validRole = new OpsRole(
                "ctam",
                60L
        );
    }

    @Test
    @DisplayName("Deve criar uma Ordem de Instrução corretamente")
    void shouldCreateInstructionOrderSucessfully() {
        InstructionOrder instructionOrder = new InstructionOrder(
                " 01ct001 ",
                2,
                " oi ctam vocom b ",
                validRole
        );

        assertAll(
                () -> assertEquals("01CT001",  instructionOrder.getCode()),
                () -> assertEquals("OI CTAM VOCOM B",  instructionOrder.getDescription()),
                () -> assertEquals(2,  instructionOrder.getQuantity()),
                () -> assertEquals("CTAM", instructionOrder.getRoleFor().getName())
        );
    }

    @Test
    @DisplayName("Não deve criar uma Ordem de Instrução sem código")
    void shouldNotCreateInstructionOrderWithoutCode() {
        IllegalArgumentException blankCode = assertThrows(IllegalArgumentException.class, () -> {
            InstructionOrder instructionOrder = new InstructionOrder(
                    "  ",
                    2,
                    " oi ctam vocom b ",
                    validRole
            );
        });

        IllegalArgumentException nullCode = assertThrows(IllegalArgumentException.class, () -> {
            InstructionOrder instructionOrder = new InstructionOrder(
                    null,
                    2,
                    " oi ctam vocom b ",
                    validRole
            );
        });
    }

    @Test
    @DisplayName("Não deve criar uma Ordem de Instrução com quantidade errada")
    void shouldNotCreateInstructionOrderWithoutQuantity() {
        IllegalArgumentException noQuantity = assertThrows(IllegalArgumentException.class, () -> {
            InstructionOrder instructionOrder = new InstructionOrder(
                    " 01ct001 ",
                    0,
                    " oi ctam vocom b ",
                    validRole
            );
        });

        IllegalArgumentException negativeQuantity = assertThrows(IllegalArgumentException.class, () -> {
            InstructionOrder instructionOrder = new InstructionOrder(
                    " 01ct001 ",
                    -1,
                    " oi ctam vocom b ",
                    validRole
            );
        });
    }

    @Test
    @DisplayName("Não atualizar uma Ordem de Instrução com parâmetros inválidos")
    void shouldNotUpdateInstructionOrderWhenDataInvalid() {
        InstructionOrder instructionOrder = new InstructionOrder(
                " 01ct001 ",
                2,
                " oi ctam vocom b ",
                validRole
        );

        IllegalArgumentException wrongCode = assertThrows(IllegalArgumentException.class, () -> {
            instructionOrder.updateDetails(
                    "",
                    2,
                    " oi ctam vocom b "
            );
        });

        IllegalArgumentException noQuantity = assertThrows(IllegalArgumentException.class, () -> {
            instructionOrder.updateDetails(
                    "01cc002",
                    0,
                    " oi ctam vocom b "
            );
        });

        IllegalArgumentException noRole = assertThrows(IllegalArgumentException.class, () -> {
            instructionOrder.assignRole(null);
        });
    }

}