package dev.arcanus.api_sgcc.modules.ops_currency.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntity;
import jakarta.persistence.*;

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

    public InstructionOrder() {
    }

    public InstructionOrder(int id, String code, int quantity, String description) {

    }

    public void setCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("O código da OI não pode ser vazio.");
        }
        this.code = code.trim().toUpperCase();
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade fechamentos/serviços deve ser positiva.");
        }
        this.quantity = quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRoleFor(OpsRole roleFor) {
        if (roleFor == null) {
            throw new IllegalArgumentException("A OI deve estar atrelada a uma qualificação operacional.");
        }
        this.roleFor = roleFor;
    }

}

