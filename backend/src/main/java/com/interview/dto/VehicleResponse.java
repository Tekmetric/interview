package com.interview.dto;

public class VehicleResponse {

    private final Long id;
    private final String make;
    private final String model;
    private final Integer year;
    private final String vin;
    private final String licensePlate;
    private final Integer mileage;

    public VehicleResponse(Long id, String make, String model, Integer year, String vin, String licensePlate, Integer mileage) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.mileage = mileage;
    }

    public Long getId() { return id; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public Integer getYear() { return year; }
    public String getVin() { return vin; }
    public String getLicensePlate() { return licensePlate; }
    public Integer getMileage() { return mileage; }
}
