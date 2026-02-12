package com.interview.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleJpaRepository extends JpaRepository<VehicleEntity, UUID> {

    boolean existsByCustomerId(UUID customerId);
}
