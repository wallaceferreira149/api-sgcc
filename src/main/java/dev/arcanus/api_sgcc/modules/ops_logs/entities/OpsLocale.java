package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "ops_locales")
public class OpsLocale extends BaseEntity {

    @Column(nullable = false,  unique = true)
    private String locale;

    protected OpsLocale() {}

    public OpsLocale(String locale) {
        setLocale(locale);
    }

    private void setLocale(String locale) {
        if (locale == null || locale.isBlank()) {
            throw new IllegalArgumentException("A localidade do registro operacional não pode ser vazia.");
        }

        this.locale = locale.trim().toUpperCase();
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OpsLocale opsLocale)) return false;

        return Objects.equals(getLocale(), opsLocale.getLocale());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLocale());
    }
}
