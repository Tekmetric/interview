package com.interview.repository;

import com.interview.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Page<Vehicle> findAll(Pageable pageable);
    boolean existsByVinAndIdNot(@NotBlank String vin, @NotBlank Long id);
}
