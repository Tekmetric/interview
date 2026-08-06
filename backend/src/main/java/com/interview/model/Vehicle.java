package com.interview.model;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Make is required")
    @Size(max = 100, message = "Make must be 100 characters or fewer")
    @Column(nullable = false)
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model must be 100 characters or fewer")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1886, message = "Year must be 1886 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    @Column(name = "year", nullable = false)
    private Integer year;

    @Size(max = 17, message = "VIN must be 17 characters or fewer")
    @Column(unique = true)
    private String vin;

    @NotNull(message = "Mileage is required")
    @Min(value = 0, message = "Mileage cannot be negative")
    @Column(nullable = false)
    private Integer mileage;

    public Vehicle() {}

    public Vehicle(String make, String model, Integer year, String vin, Integer mileage) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.mileage = mileage;
    }

    public Long getId() { return id; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
}
