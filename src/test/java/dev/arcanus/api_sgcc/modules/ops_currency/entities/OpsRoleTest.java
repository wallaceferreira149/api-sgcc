package dev.arcanus.api_sgcc.modules.ops_currency.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpsRoleTest {

    @Test
    void shouldCreateRoleSuccessfully() {
        OpsRole role = new OpsRole(
                "cc",
                120L
        );

        assertEquals("CC", role.getName());
        assertEquals(120L, role.getDaysToExpire());

    }

    @Test
    void shouldNotAllowBlankName() {
        OpsRole role = new OpsRole();
        IllegalArgumentException notNullName = assertThrows(IllegalArgumentException.class, () -> {
             role.setName(null);
        });
        IllegalArgumentException notBlankName = assertThrows(IllegalArgumentException.class, () -> {
             role.setName("");
        });
        IllegalArgumentException nameWithSpaces = assertThrows(IllegalArgumentException.class, () -> {
            role.setName("   ");
        });

    }

    @Test
    void shouldNotAllowWrongDaysToExpire() {
        OpsRole role = new OpsRole();

        IllegalArgumentException notNullDaysToExpire = assertThrows(IllegalArgumentException.class, () -> {
            role.setDaysToExpire(null);
        });
        IllegalArgumentException notNegativeDaysToExpire = assertThrows(IllegalArgumentException.class, () -> {
            role.setDaysToExpire(-1L);
        });
        IllegalArgumentException notZeroDaysToExpire = assertThrows(IllegalArgumentException.class, () -> {
            role.setDaysToExpire(0L);
        });
    }

    @Test
    void shouldNotAllowWrongDescription(){
        OpsRole role = new OpsRole();

        role.setDescription(null);
        assertEquals("", role.getDescription());

        role.setDescription("   ");
        assertEquals("", role.getDescription());

        role.setDescription(" Comando Aéreo ");
        assertEquals("comando aéreo", role.getDescription());
    }
}