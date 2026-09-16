package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntityAudit;
import dev.arcanus.api_sgcc.modules.ops_logs.value_objects.IFF;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "operational_logs")
public class OperationalLog extends BaseEntityAudit {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operator_id", nullable = false)
    private OpsUser operator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assistant_id")
    private OpsUser assistant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private OpsUser instructor;

    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "locale_id", nullable = false)
    private OpsLocale locale;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instruction_order_id", nullable = false)
    private InstructionOrder instructionOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ops_role_id", nullable = false)
    private OpsRole opsRole;

    private boolean isReal;

    private int quantityRegistred;

    private String airCraftCallSign;

    private String airCraftType;

    private int airCraftQuantity;

    private void setAirCraftQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade de aeronaves deve ser positiva.");
        }
        this.airCraftQuantity = quantity;
    }

    @Embedded
    private IFF airCraftIFF;

    private Instant controlStartedAt;

    private Instant controlFinishedAt;

    private int controlDuration;



}
