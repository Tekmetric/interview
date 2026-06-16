package com.interview.application.vehicle;

import com.interview.application.CustomerRepository;
import com.interview.application.InvalidReferenceException;
import com.interview.application.VehicleRepository;
import com.interview.domain.Vehicle;

import java.util.UUID;

public class CreateVehicle {

    private final VehicleRepository repository;
    private final CustomerRepository customerRepository;

    public CreateVehicle(VehicleRepository repository, CustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    public Vehicle execute(String plateNumber, String model, UUID customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new InvalidReferenceException("Customer not found: " + customerId));
        Vehicle vehicle = new Vehicle(UUID.randomUUID(), plateNumber, model, customerId);
        return repository.save(vehicle);
    }
}
