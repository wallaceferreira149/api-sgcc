package dev.arcanus.api_sgcc.modules.ops_logs.value_objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Embeddable
public class FlightControl {

    private Instant controlStartedAt;

    private Instant controlEndAt;

    @Embedded
    private FlightInfo flightInfo;

    protected FlightControl() {}

    private FlightControl(Instant controlStartedAt, Instant controlEndAt, FlightInfo flightInfo) {
        this.controlStartedAt = controlStartedAt;
        this.controlEndAt = controlEndAt;
        this.flightInfo = Objects.requireNonNull(
            flightInfo,
            "As informações das aeronaves controladas devem ser informadas."
        );
    }

    public static FlightControl of(Instant controlStartedAt, Instant controlEndAt, FlightInfo flightInfo) {
        validateTimes(controlStartedAt, controlEndAt);
        return new FlightControl(
          controlStartedAt,
          controlEndAt,
          flightInfo
        );
    }

    private static void validateTimes(Instant controlStartedAt, Instant controlEndAt) {
        if (controlStartedAt == null) {
            throw new IllegalArgumentException("O horário do controle assumido deve ser informado.");
        }

        if (controlEndAt == null) {
            throw new IllegalArgumentException("O horário do controle encerrado deve ser informado.");
        }

        if (!controlStartedAt.isBefore(controlEndAt)) {
            throw new IllegalArgumentException("O horário do controle assumido deve ser anterior ao controle encerrado");

        }
    }

    public Duration getControlDuration() {
        return Duration.between(controlStartedAt, controlEndAt);
    }

    public FlightInfo getFlightInfo() {
        return this.flightInfo;
    }

    public Instant getControlStartedAt() {
        return controlStartedAt;
    }

    public Instant getControlEndAt() {
        return controlEndAt;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof FlightControl that)) return false;

        return Objects.equals(controlStartedAt, that.controlStartedAt) && Objects.equals(controlEndAt, that.controlEndAt) && Objects.equals(getFlightInfo(), that.getFlightInfo());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(controlStartedAt);
        result = 31 * result + Objects.hashCode(controlEndAt);
        result = 31 * result + Objects.hashCode(getFlightInfo());
        return result;
    }

    @Override
    public String toString() {
        return "FlightControl{" +
            "controlStartedAt=" + controlStartedAt +
            ", controlEndAt=" + controlEndAt +
            ", flightInfo=" + flightInfo +
            '}';
    }
}
