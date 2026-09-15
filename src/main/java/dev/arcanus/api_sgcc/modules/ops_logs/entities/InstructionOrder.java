package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntity;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "ops_instruction_orders")
public class InstructionOrder extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private int quantity;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ops_role_id", nullable = false)
    private OpsRole roleFor;

    protected InstructionOrder() {
    }

    public InstructionOrder(String code, int quantity, String description,  OpsRole roleFor) {
        setCode(code);
        setQuantity(quantity);
        setDescription(description);
        assignRole(roleFor);
    }

    // --- MÉTODOS DE NEGÓCIO E ALTERAÇÃO DE ESTADO ---

    public void updateDetails(String code, int quantity, String description) {
        setCode(code);
        setQuantity(quantity);
        setDescription(description);
    }

    public void assignRole(OpsRole roleFor) {
        if (roleFor == null) {
            throw new IllegalArgumentException("A OI deve estar atrelada a uma qualificação operacional.");
        }
        this.roleFor = roleFor;
    }

    // --- MUTADORES PRIVADOS ---

    private void setCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("O código da OI não pode ser vazio.");
        }
        this.code = code.trim().toUpperCase();
    }

    private void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade fechamentos/serviços deve ser positiva.");
        }
        this.quantity = quantity;
    }

    private void setDescription(String description) {
        this.description = description == null ? null : description.trim().toUpperCase();
    }

    // --- GETTERS ---

    public String getCode() {
        return code;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public OpsRole getRoleFor() {
        return roleFor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstructionOrder that)) return false;

        // Se a entidade tem ID em BaseEntity, compara pelo ID; caso contrário, pela chave de negócio única (code)
        if (getId() != null && that.getId() != null) {
            return Objects.equals(getId(), that.getId());
        }
        return Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hash(getId()) : Objects.hash(getCode());
    }
}

