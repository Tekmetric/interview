package com.interview.converter;

import com.interview.domain.Vehicle;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;

public class VehicleConverter {

    private VehicleConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static Vehicle toVehicle(VehicleRequest vehicleRequest) {
        Vehicle vehicle = new Vehicle();

        vehicle.setBrand(vehicleRequest.getBrand());
        vehicle.setModel(vehicleRequest.getModel());
        vehicle.setMadeYear(vehicleRequest.getMadeYear());
        vehicle.setColor(vehicleRequest.getColor());
        vehicle.setOwnerId(vehicleRequest.getOwnerId());

        return vehicle;
    }

    public static VehicleResponse toVehicleResponse(Vehicle vehicle) {
        VehicleResponse vehicleResponse = new VehicleResponse();

        vehicleResponse.setId(vehicle.getId());
        vehicleResponse.setCreatedAt(vehicle.getCreatedAt());
        vehicleResponse.setUpdatedAt(vehicle.getUpdatedAt());
        vehicleResponse.setBrand(vehicle.getBrand());
        vehicleResponse.setModel(vehicle.getModel());
        vehicleResponse.setMadeYear(vehicle.getMadeYear());
        vehicleResponse.setColor(vehicle.getColor());
        vehicleResponse.setOwnerId(vehicle.getOwnerId());

        return vehicleResponse;
    }

    public static Vehicle apply(Vehicle vehicle, VehicleRequest changes) {
        vehicle.setBrand(changes.getBrand());
        vehicle.setModel(changes.getModel());
        vehicle.setMadeYear(changes.getMadeYear());
        vehicle.setColor(changes.getColor());
        vehicle.setOwnerId(changes.getOwnerId());

        return vehicle;
    }
}
