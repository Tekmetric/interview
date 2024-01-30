package com.interview.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.interview.model.Vehicle;
import com.interview.model.VehicleState;
import com.interview.resource.dto.VehicleCreationRequest;
import com.interview.resource.dto.VehicleDto;
import com.interview.resource.dto.VehicleStateDto;
import com.interview.resource.dto.VehicleUpdateRequest;

@Service
public class VehicleMapper {

    public Vehicle toDomainEntity(VehicleCreationRequest request) {
        return Vehicle.builder()
            .licensePlate(request.getLicensePlate())
            .brand(request.getBrand())
            .model(request.getModel())
            .registrationYear(request.getRegistrationYear())
            .cost(request.getCost() != null ? request.getCost() : 0)
            .build();
    }

    public Vehicle toDomainEntity(Vehicle vehicle, VehicleUpdateRequest request) {
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setRegistrationYear(request.getRegistrationYear());
        vehicle.setCost(request.getCost());
        vehicle.setUpdatedAt(Instant.now());
        return vehicle;
    }

    public VehicleDto toDto(Vehicle vehicle) {
        return VehicleDto.builder()
            .id(vehicle.getId())
            .licensePlate(vehicle.getLicensePlate())
            .state(mapState(vehicle.getState()))
            .brand(vehicle.getBrand())
            .model(vehicle.getModel())
            .registrationYear(vehicle.getRegistrationYear())
            .cost(vehicle.getCost())
            .creationDate(Date.from(vehicle.getCreatedAt()))
            .lastModificationDate(Date.from(vehicle.getUpdatedAt()))
            .build();
    }

    private VehicleStateDto mapState(VehicleState state) {
        return VehicleStateDto.valueOf(state.name());
    }
}
