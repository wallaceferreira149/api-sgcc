package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntity;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "ops_roles")
public class OpsRole extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private Long daysToExpire;

    @OneToMany(mappedBy = "roleFor", fetch = FetchType.LAZY)
    private Set<InstructionOrder> instructionOrders = new HashSet<>();

    public OpsRole() {}

    public OpsRole(String name, Long daysToExpire) {
        setName(name);
        setDaysToExpire(daysToExpire);
    }

    public OpsRole(String name, String description, Long daysToExpire) {
        setName(name);
        setDescription(description);
        setDaysToExpire(daysToExpire);
    }

    public String getName() {
        return name;
    }

    public Long getDaysToExpire() {
        return daysToExpire;
    }

    public String getDescription() {
        return this.description;
    }

    public Set<InstructionOrder> getInstructionOrders() {
        return Collections.unmodifiableSet(this.instructionOrders);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A qualificação operacional é obrigatória.");
        }
        this.name = name.trim().toUpperCase();
    }

    public void setDaysToExpire(Long daysToExpire) {
        if (daysToExpire == null || daysToExpire <= 0 ) {
            throw new IllegalArgumentException("A duração deve ser um período positivo.");
        }
        this.daysToExpire = daysToExpire;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim().toLowerCase() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        OpsRole opsRole = (OpsRole) o;
        return getId() == opsRole.getId() && getName().equals(opsRole.getName()) && Objects.equals(getDescription(), opsRole.getDescription());
    }

    @Override
    public int hashCode() {
        int result = this.getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + Objects.hashCode(getDescription());
        return result;
    }
}
