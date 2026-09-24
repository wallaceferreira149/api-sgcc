package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftTypeTest {

    private AircraftType aircraftType;

    @BeforeEach
    void setUp() {
        aircraftType = new AircraftType(
            "a-29"
        );
    }

    @Test
    @DisplayName("Deve criar uma nova aeronave com sucesso")
    void shouldCreateAircraftTypeSucessfully() {
        assertEquals("A-29", aircraftType.getCode());
    }

    @Test
    @DisplayName("O código da aeronave deve existir")
    void shouldThrowExceptionWhenCodeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftType(null));
        assertThrows(IllegalArgumentException.class, () -> new AircraftType(""));
    }

}
