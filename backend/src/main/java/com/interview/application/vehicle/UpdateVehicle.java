package com.interview.application.vehicle;

import com.interview.application.CustomerRepository;
import com.interview.application.EntityNotFoundException;
import com.interview.application.InvalidReferenceException;
import com.interview.application.VehicleRepository;
import com.interview.domain.Vehicle;

import java.util.UUID;

public class UpdateVehicle {

    private final VehicleRepository repository;
    private final CustomerRepository customerRepository;

    public UpdateVehicle(VehicleRepository repository, CustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    public Vehicle execute(UUID id, String plateNumber, String model, UUID customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new InvalidReferenceException("Customer not found: " + customerId));
        Vehicle existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + id));
        Vehicle updated = new Vehicle(existing.getId(), plateNumber, model, customerId);
        return repository.save(updated);
    }
}
