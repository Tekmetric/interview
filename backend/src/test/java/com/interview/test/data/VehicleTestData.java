package com.interview.test.data;

import com.interview.domain.Vehicle;
import com.interview.dto.VehicleRequest;

public class VehicleTestData {

    private static final String BRAND = "Ford";
    private static final String MODEL = "F-150";
    private static final int YEAR = 2020;
    private static final String COLOR = "red";

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
