package com.interview.mapper;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.entity.Vehicle;

public final class VehicleMapper {
    public static Vehicle toEntity(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setModelYear(request.getModelYear());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setVin(request.getVin());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setDoors(request.getDoors());
        vehicle.setMileage(request.getMileage());
        return vehicle;
    }

    public static VehicleResponse toResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setCreatedAt(vehicle.getCreatedAt());
        response.setModelYear(vehicle.getModelYear());
        response.setMake(vehicle.getMake());
        response.setModel(vehicle.getModel());
        response.setColor(vehicle.getColor());
        response.setLicensePlate(vehicle.getLicensePlate());
        response.setVin(vehicle.getVin());
        response.setFuelType(vehicle.getFuelType());
        response.setDoors(vehicle.getDoors());
        response.setMileage(vehicle.getMileage());
        return response;
    }
}
