package com.interview.test.data;

import com.interview.domain.Vehicle;
import com.interview.dto.VehicleRequest;

public class VehicleTestData {

    public static final String BRAND = "Ford";
    public static final String MODEL = "F-150";
    public static final int YEAR = 2020;
    public static final String COLOR = "red";

    public static VehicleRequest vehicleRequest() {
        VehicleRequest vehicleRequest = new VehicleRequest();
        vehicleRequest.setBrand(BRAND);
        vehicleRequest.setModel(MODEL);
        vehicleRequest.setMadeYear(YEAR);
        vehicleRequest.setColor(COLOR);
        return vehicleRequest;
    }

    public static Vehicle vehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(BRAND);
        vehicle.setModel(MODEL);
        vehicle.setMadeYear(YEAR);
        vehicle.setColor(COLOR);
        return vehicle;
    }
}
