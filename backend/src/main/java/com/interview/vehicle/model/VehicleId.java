package com.interview.vehicle.model;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

public record VehicleId(@NotNull long value) implements Serializable {

    public static VehicleId fromValue(long value) {
        return new VehicleId(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleId that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
