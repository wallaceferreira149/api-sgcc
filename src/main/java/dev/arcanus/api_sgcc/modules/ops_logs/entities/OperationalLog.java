package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntityAudit;
import dev.arcanus.api_sgcc.modules.ops_logs.value_objects.IFF;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "operational_logs")
public class OperationalLog extends BaseEntityAudit {

    private OpsUser operator;

    private OpsUser assistant;

    private String instructor;

    private OpsLocale locale;

    private InstructionOrder instructionOrder;

    private OpsRole opsRole;

    private boolean isReal;

    private int quantityRegistred;

    private String airCraftCallSign;

    private String airCraftType;

    private int airCraftQuantity;

    private IFF airCraftIFF;

    private Instant controlStartedAt;

    private Instant controlFinishedAt;

    private int controlDuration;



}
