package com.interview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VehicleRequest {

    @NotBlank(message = "make is required")
    @Size(max = 100, message = "make must be at most 100 characters")
    private String make;

    @NotBlank(message = "model is required")
    @Size(max = 100, message = "model must be at most 100 characters")
    private String model;

    @NotNull(message = "year is required")
    @Min(value = 1886, message = "year must be 1886 or later")
    @Max(value = 2100, message = "year must be 2100 or earlier")
    private Integer year;

    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "vin must be a valid 17-character VIN")
    private String vin;

    @Size(max = 20, message = "licensePlate must be at most 20 characters")
    private String licensePlate;

    @Min(value = 0, message = "mileage must be zero or greater")
    private Integer mileage;

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
}
