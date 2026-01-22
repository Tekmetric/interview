package com.interview.mapper;

import com.interview.dto.VehicleDTO;
import com.interview.model.Vehicle;

public class VehicleMapper {

    public static VehicleDTO toDto(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setVin(vehicle.getVin());
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setModelYear(vehicle.getModelYear());
        if (vehicle.getCustomer() != null) {
            dto.setCustomerId(vehicle.getCustomer().getId());
        }
        return dto;
    }

    public static Vehicle toEntity(VehicleDTO dto) {
        if (dto == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setId(dto.getId());
        vehicle.setVin(dto.getVin());
        vehicle.setMake(dto.getMake());
        vehicle.setModel(dto.getModel());
        vehicle.setModelYear(dto.getModelYear());
        // Note: The Customer entity is not set here. It should be set in the service layer.
        return vehicle;
    }
}
