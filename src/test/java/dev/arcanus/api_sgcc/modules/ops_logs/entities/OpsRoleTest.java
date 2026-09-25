package dev.arcanus.api_sgcc.modules.ops_logs.entities;

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

        assertThrows(
                IllegalArgumentException.class,
                () -> role.updateDetails("CC", null, null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> role.updateDetails("CC", null, -1L)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> role.updateDetails("CC", null, 0L)
        );
    }

    @Test
    void shouldNormalizeDescription() {
        OpsRole role = new OpsRole("CC", 120L);

        role.updateDetails("CC", null, 120L);
        assertEquals("", role.getDescription());

        role.updateDetails("CC", "   ", 120L);
        assertEquals("", role.getDescription());

        role.updateDetails("CC", " Comando Aéreo ", 120L);
        assertEquals("comando aéreo", role.getDescription());
    }

    @Test
    void shouldNotCreateRoleWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsRole(null, 120L);
        });
    }

    @Test
    void shouldNotCreateRoleWithBlankName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsRole("   ", 120L);
        });
    }

    @Test
    void shouldNotCreateRoleWithNullDaysToExpire() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsRole("CC", null);
        });
    }

    @Test
    void shouldNotCreateRoleWithZeroDaysToExpire() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsRole("CC", 0L);
        });
    }

    @Test
    void shouldNotCreateRoleWithNegativeDaysToExpire() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsRole("CC", -30L);
        });
    }

    @Test
    void shouldNormalizeNameToUpperCase() {
        OpsRole role = new OpsRole(
                " cc ",
                90L
        );

        assertEquals("CC", role.getName());
    }
}
