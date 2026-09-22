package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntity;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@Table(name = "ops_users")
public class OpsUser extends BaseEntity {

    private static final Pattern INSTITUTIONAL_EMAIL =
        Pattern.compile("^[a-z0-9._%+-]+@fab\\.mil\\.br$");

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ops_role_id")
    private OpsRole opsRole;

    protected OpsUser() {}

    public OpsUser(String email, OpsRole opsRole) {
        assignEmail(email);
        assignRole(opsRole);
    }

//    VALIDAÇÕES

    private void assignRole(OpsRole opsRole) {
        if (opsRole == null) {
            throw new IllegalArgumentException("O papel do operador não pode ser nulo.");
        }
        this.opsRole = opsRole;
    }

    private void assignEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O email não pode ser nulo ou vazio.");
        }

        String normalized = email.trim().toLowerCase();

        if (!INSTITUTIONAL_EMAIL.matcher(normalized).matches()) {
            throw new IllegalArgumentException("O email deve ser institucional (@fab.mil.br).");
        }
        this.email = normalized;
    }

//     ALTERAÇÕES
    public void changeEmail(String email) {
        assignEmail(email);
    }

    public void changeRole(OpsRole opsRole) {
        assignRole(opsRole);
    }

//    GETTERS

    public String getEmail() {
        return email;
    }

    public OpsRole getOpsRole() {
        return opsRole;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OpsUser opsUser)) return false;

        return Objects.equals(getEmail(), opsUser.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEmail());
    }

    @Override
    public String toString() {
        return email;
    }
}
