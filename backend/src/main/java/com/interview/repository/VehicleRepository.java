package com.interview.repository;

import com.interview.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Optional<Vehicle> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByVinAndDeletedAtIsNull(String vin);

    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.deletedAt IS NULL
            AND (:make IS NULL OR LOWER(v.make) = LOWER(:make))
            AND (:year IS NULL OR v.year = :year)
            """)
    Page<Vehicle> findAll(@Param("make") String make,
                          @Param("year") Integer year,
                          Pageable pageable);
}