package com.interview.domain;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "vehicle")
public class Vehicle {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    private long createdAt;
    private long updatedAt;
    private String brand;
    private String model;
    private int madeYear;
    private String color;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID ownerId;

    @PrePersist
    public void prePersist() {
        long now = Instant.now().toEpochMilli();
        if (this.createdAt == 0) {
            this.createdAt = now;
        }
        if (this.updatedAt == 0) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

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
        Vehicle vehicle = (Vehicle) o;
        return createdAt == vehicle.createdAt && updatedAt == vehicle.updatedAt && madeYear == vehicle.madeYear && Objects.equals(id, vehicle.id) && Objects.equals(brand, vehicle.brand) && Objects.equals(model, vehicle.model) && Objects.equals(color, vehicle.color) && Objects.equals(ownerId, vehicle.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, brand, model, madeYear, color, ownerId);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", madeYear=" + madeYear +
                ", color='" + color + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }
}
