package dev.arcanus.api_sgcc.modules.ops_logs.value_objects;

import dev.arcanus.api_sgcc.modules.ops_logs.entities.AircraftType;
import jakarta.persistence.*;

import java.util.Objects;

@Embeddable
public class FlightInfo {

    @Column(name = "aircraft_callsign")
    private String aircraftCallsign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_type_id")
    private AircraftType aircraftType;

    @Column(name = "aircraft_quantity")
    private int aircraftQuantity;

    @Embedded
    private IFF aircraftIFF;

    protected FlightInfo() {}

    private FlightInfo(String aircraftCallsign, AircraftType aircraftType, int aircraftQuantity, String aircraftIFF) {
        this.aircraftCallsign = aircraftCallsign != null ? aircraftCallsign.trim().toUpperCase() : null;
        this.aircraftType = aircraftType;
        this.aircraftQuantity = aircraftQuantity;
        this.aircraftIFF = IFF.of(aircraftIFF);
    }

    public static FlightInfo of(String aircraftCallSign, AircraftType aircraftType, int aircraftQuantity, String aircraftIFF) {
        validate(aircraftQuantity);
        return new FlightInfo(aircraftCallSign, aircraftType, aircraftQuantity, aircraftIFF);
    }

    private static void validate(int aircraftQuantity) {
        if (aircraftQuantity <= 0 || aircraftQuantity > 99) {
            throw new IllegalArgumentException("A quantidade de aeronaves deve estar entre 1 e 99.");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FlightInfo other)) {
            return false;
        }

        return Objects.equals(aircraftCallsign, other.aircraftCallsign)
            && Objects.equals(aircraftType, other.aircraftType)
            && aircraftQuantity == other.aircraftQuantity
            && Objects.equals(aircraftIFF, other.aircraftIFF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            aircraftCallsign,
            aircraftType,
            aircraftQuantity,
            aircraftIFF
        );
    }

    @Override
    public String toString() {
        return "FlightInfo{" +
            "aircraftCallsign='" + aircraftCallsign + '\'' +
            ", aircraftType='" + aircraftType + '\'' +
            ", aircraftQuantity=" + aircraftQuantity +
            ", aircraftIFF=" + aircraftIFF +
            '}';
    }

}
