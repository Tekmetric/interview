package com.interview.application.vehicle;

import com.interview.application.EntityNotFoundException;
import com.interview.application.VehicleRepository;
import com.interview.domain.Vehicle;

import java.util.UUID;

public class GetVehicle {

    private final VehicleRepository repository;

    public GetVehicle(VehicleRepository repository) {
        this.repository = repository;
    }

    public Vehicle execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + id));
    }
}
