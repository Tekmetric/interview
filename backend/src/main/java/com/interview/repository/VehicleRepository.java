package com.interview.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByIdAndDeletedAtIsNull(Long id);

    Optional<Vehicle> findByLicensePlateAndDeletedAtIsNull(String licensePlate);

    Page<Vehicle> findByDeletedAtIsNull(Pageable pageable);
}
