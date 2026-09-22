package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpsUserTest {

    private OpsRole validRole() {
        return new OpsRole("CC", 120L);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        OpsUser user = new OpsUser(
                "Fulano.Silva@fab.mil.br ",
                validRole()
        );

        assertEquals("fulano.silva@fab.mil.br", user.getEmail());
        assertEquals("CC", user.getOpsRole().getName());
    }

    @Test
    void shouldNotCreateUserWithNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsUser(null, validRole());
        });
    }

    @Test
    void shouldNotCreateUserWithBlankEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsUser("   ", validRole());
        });
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsUser("not-an-email", validRole());
        });
    }

    @Test
    void shouldNotCreateUserWithNonInstitutionalEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsUser("fulano@gmail.com", validRole());
        });
    }

    @Test
    void shouldNotCreateUserWithNullRole() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OpsUser("fulano@fab.mil.br", null);
        });
    }

    @Test
    void shouldNotChangeEmailToInvalid() {
        OpsUser user = new OpsUser("fulano@fab.mil.br", validRole());

        assertThrows(IllegalArgumentException.class, () -> {
            user.changeEmail("fulano@gmail.com");
        });
        assertEquals("fulano@fab.mil.br", user.getEmail());
    }

    @Test
    void shouldNotChangeRoleToNull() {
        OpsUser user = new OpsUser("fulano@fab.mil.br", validRole());

        assertThrows(IllegalArgumentException.class, () -> {
            user.changeRole(null);
        });
        assertEquals("CC", user.getOpsRole().getName());
    }

    @Test
    void shouldBeEqualByEmail() {
        OpsUser first = new OpsUser("fulano@fab.mil.br", validRole());
        OpsUser second = new OpsUser("FULANO@FAB.MIL.BR", validRole());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualWithDifferentEmail() {
        OpsUser first = new OpsUser("fulano@fab.mil.br", validRole());
        OpsUser second = new OpsUser("ciclano@fab.mil.br", validRole());

        assertNotEquals(first, second);
    }
}
