package com.interview.dto;

import java.util.Objects;
import java.util.UUID;

public class VehicleRequest {

    private String brand;
    private String model;
    private int madeYear;
    private String color;
    private UUID ownerId;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMadeYear() {
        return madeYear;
    }

    public void setMadeYear(int madeYear) {
        this.madeYear = madeYear;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VehicleRequest that = (VehicleRequest) o;
        return madeYear == that.madeYear && Objects.equals(brand, that.brand) && Objects.equals(model, that.model) && Objects.equals(color, that.color) && Objects.equals(ownerId, that.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, model, madeYear, color, ownerId);
    }

    @Override
    public String toString() {
        return "VehicleRequest{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", madeYear=" + madeYear +
                ", color='" + color + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }
}
