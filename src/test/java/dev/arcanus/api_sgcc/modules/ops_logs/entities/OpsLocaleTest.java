package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class OpsLocaleTest {


    @Test
    @DisplayName("Deve criar uma localidade com sucesso")
    void shouldCreateLocaleSuccessfully() {
        OpsLocale opsLocale = new OpsLocale(
                "icea"
        );
        assertEquals("ICEA", opsLocale.getLocale());
    }

    @Test
    @DisplayName("Não deve criar uma localidade em branco")
    void shouldNotCreateEmptyLocale() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            OpsLocale locale = new OpsLocale("  ");
        });
    }
}