package com.interview.application.vehicle;

import com.interview.application.VehicleRepository;
import com.interview.domain.Vehicle;

import java.util.List;

public class ListVehicles {

    private final VehicleRepository repository;

    public ListVehicles(VehicleRepository repository) {
        this.repository = repository;
    }

    public List<Vehicle> execute() {
        return repository.findAll();
    }
}
