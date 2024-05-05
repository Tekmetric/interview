package com.interview.model.api;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateVehicleRequest {

    @NotNull(message = "Vehicle make cannot be null")
    @Size(max = 100, message = "Vehicle make must be no more than 100 characters")
    private String make;
    @NotNull(message = "Vehicle model cannot be null")
    @Size(max = 100, message = "Vehicle model must be no more than 100 characters")
    private String model;
    @Min(value = 0, message = "Vehicle year must be non-negative")
    private long year;
    @NotNull(message = "Vin cannot be null")
    @Size(max = 17, message = "Vin must be no more than 17 characters")
    private String vin;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
}
