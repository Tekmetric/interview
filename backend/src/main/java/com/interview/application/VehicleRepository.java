package com.interview.application;

import com.interview.domain.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findById(UUID id);

    List<Vehicle> findAll();

    boolean existsByCustomerId(UUID customerId);

    void deleteById(UUID id);
}
