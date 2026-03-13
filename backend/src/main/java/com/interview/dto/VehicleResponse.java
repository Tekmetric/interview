package com.interview.dto;

import com.interview.model.Vehicle;
import lombok.Data;

@Data
public class VehicleResponse {

    private Long id;
    private String vin;
    private String make;
    private String model;
    private int year;

    public VehicleResponse(Vehicle v) {
        this.id = v.getId();
        this.vin = v.getVin();
        this.make = v.getMake();
        this.model = v.getModel();
        this.year = v.getYear();
    }
}
