package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "aircraft_types")
public class AircraftType extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String code;

    protected AircraftType() {}

    public  AircraftType(String code) {
        normalize(code);
    }

    private void normalize(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("O tipo de aeronave não pode ser vazio.");
        }

        this.code = code.trim().toUpperCase();
    }

    public String getCode() {
        return this.code;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AircraftType that)) return false;

        return Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCode());
    }

    @Override
    public String toString() {
        return this.code;
    }
}
