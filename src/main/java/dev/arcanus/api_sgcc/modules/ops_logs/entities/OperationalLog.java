package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntityAudit;
import dev.arcanus.api_sgcc.modules.ops_logs.value_objects.FlightControl;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    @Column(name = "log_date", nullable = false)
    private Instant opslogDate;

    @Column(nullable = false)
    private boolean isReal;

    @Column(nullable = false)
    private int quantityRegistered;

    @Embedded
    private FlightControl flightControl;

    protected OperationalLog() {}

    private OperationalLog(Builder builder) {
        this.operator = Objects.requireNonNull(builder.operator);
        this.assistant = builder.assistant;
        this.instructor = builder.instructor;
        this.locale = Objects.requireNonNull(builder.locale);
        this.instructionOrder = Objects.requireNonNull(builder.instructionOrder);
        this.opsRole = Objects.requireNonNull(builder.opsRole);
        this.opslogDate = Objects.requireNonNull(builder.opslogDate);
        this.isReal = builder.isReal;
        this.quantityRegistered = builder.quantityRegistered;
        this.flightControl = builder.flightControl;
        validateCrew();
        validateFlightControl();
        checkQuantity();
    }

    public static final class Builder {
        private OpsUser operator, assistant, instructor;
        private OpsLocale locale;
        private InstructionOrder instructionOrder;
        private OpsRole opsRole;
        private boolean isReal;
        private Instant opslogDate;
        private int quantityRegistered;
        private FlightControl flightControl;

        public Builder operator(OpsUser operator) {
            if (operator == null) {
                throw new IllegalArgumentException("O operador não pode ser nulo.");
            }
            this.operator = operator;
            return this;
        }
        public Builder assistant(OpsUser assistant) {
            this.assistant = assistant;
            return this;
        }
        public Builder instructor(OpsUser instructor) {
            this.instructor = instructor;
            return this;
        }
        public Builder locale(OpsLocale locale) {
            if (locale == null) {
                throw new IllegalArgumentException("A localidade é obrigatória.");
            }
            this.locale = locale;
            return this;
        }

        public Builder instructionOrder(InstructionOrder instructionOrder) {
            if (instructionOrder == null) {
                throw new IllegalArgumentException("A ordem de instrução não pode ser nulo.");
            }
            this.instructionOrder = instructionOrder;
            return this;
        }

        public Builder opsRole(OpsRole opsRole) {
            if (opsRole == null) {
                throw new IllegalArgumentException("O papel do operador não pode ser nulo.");
            }
            this.opsRole = opsRole;
            return this;
        }

        public Builder opslogDate(Instant opslogDate) {
            if (opslogDate == null) {
                throw new IllegalArgumentException("A data do registro operacional não pode ser nulo.");
            }
            this.opslogDate = opslogDate;
            return this;
        }

        public Builder isReal(boolean isReal) {
            this.isReal = isReal;
            return this;
        }

        public Builder quantityRegistered(int quantityRegistered) {
            this.quantityRegistered = quantityRegistered;
            return this;
        }

        public Builder flightControl(FlightControl flightControl) {
            this.flightControl = flightControl;
            return this;
        }


        public OperationalLog build() {
            return new OperationalLog(this);
        }
    }

//    VALIDAÇÕES
    private void validateCrew() {
        if (operator.equals(assistant) || operator.equals(instructor) || (assistant != null && assistant.equals(instructor))) {
            throw new IllegalArgumentException("Um usuário não pode desempenhar mais de uma função para uma mesma manutenção operacional");
        }
    }

    private void checkQuantity() {
        if (quantityRegistered <= 0) {
            throw new IllegalArgumentException("A quantidade de aeronaves deve ser positiva.");
        }
    }

    private void validateFlightControl() {
        if (isReal && flightControl == null) {
            throw new IllegalArgumentException("O controle de voo deve ser definido para operações reais.");
        }
        if (!isReal && flightControl != null) {
            throw new IllegalArgumentException("O controle de voo só pode ser definido para operações reais.");
        }
    }

    public void reassignCrew(OpsUser assistant, OpsUser instructor) {
        if (assistant == null && instructor == null) {
            throw new IllegalArgumentException("Um dos parâmetros deve ser preenchido.");
        }

        this.assistant = assistant;
        this.instructor = instructor;
        validateCrew();
    }


//    GETTERS

    public List<OpsUser> getCrew() {
        return Stream.of(operator, assistant, instructor).filter(Objects::nonNull).toList();
    }

    public OpsUser getOperator() {
        return operator;
    }

    public OpsUser getAssistant() {
        return assistant;
    }

    public OpsUser getInstructor() {
        return instructor;
    }

    public OpsLocale getLocale() {
        return locale;
    }

    public InstructionOrder getInstructionOrder() {
        return instructionOrder;
    }

    public OpsRole getOpsRole() {
        return opsRole;
    }

    public Instant getOpslogDate() {
        return opslogDate;
    }

    public FlightControl getFlightControl(){
        return flightControl;
    }

    public int getQuantityRegistered() {
        return quantityRegistered;
    }

    public boolean isReal() {
        return isReal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationalLog other)) return false;
        if (getId() != null && other.getId() != null) {
            return Objects.equals(getId(), other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hash(getId()) : System.identityHashCode(this);
    }

}
