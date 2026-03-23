package com.interview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_id_seq")
    @SequenceGenerator(
            name = "vehicle_id_seq",
            sequenceName = "vehicle_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @Min(1900)
    @NotNull
    private Integer modelYear;

    @NotBlank
    private String make;

    @NotBlank
    private String model;

    private String color;

    @Pattern(regexp = "^[A-Z0-9 -]{1,8}$")
    private String licensePlate;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{17}$")
    private String vin;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Min(0) // e.g. Motorcycles have no doors
    private Integer doors;

    @Min(0)
    private Integer mileage;
}
