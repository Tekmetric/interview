package com.interview.vehicle.persistence.entity;

import com.interview.vehicle.model.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

import java.time.Year;

@Entity
@Table(name = "vehicles")
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleEntity implements Vehicle {

    @Id
    @Getter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long rawId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private VehicleType type;

    @Column(name = "fabrication_year")
    private Year fabricationYear;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Transient
    private VehicleId id;

    public VehicleEntity(VehicleType type,
                         Year fabricationYear,
                         String make,
                         String model) {
        Assert.notNull(type, "Vehicle type cannot be null");
        Assert.notNull(make, "Vehicle make cannot be null");
        Assert.notNull(model, "Vehicle model cannot be null");
        Assert.isTrue(fabricationYear.getValue() >= 1900, "Year of fabrication should not be older than 1900");

        this.type = type;
        this.fabricationYear = fabricationYear;
        this.make = make;
        this.model = model;
    }

    @PostLoad
    private void initIdAfterLoad() {
        this.id = VehicleId.fromValue(this.rawId);
    }

    @PostPersist
    private void initIdAfterPersist() {
        this.id = VehicleId.fromValue(this.rawId);
    }

    public static VehicleEntity from(VehicleCreate create) {
        return new VehicleEntity(
                create.type(),
                create.fabricationYear(),
                create.make(),
                create.model());
    }

    @Override
    public void applyUpdate(VehicleUpdate update) {
        Assert.notNull(update.type(), "Vehicle type cannot be null");
        Assert.notNull(update.make(), "Vehicle make cannot be null");
        Assert.notNull(update.make(), "Vehicle model cannot be null");
        Assert.isTrue(update.fabricationYear().getValue() >= 1900, "Year of fabrication should not be older than 1900");

        this.type = update.type();
        this.fabricationYear = update.fabricationYear();
        this.make = update.make();
        this.model = update.model();
    }
}
