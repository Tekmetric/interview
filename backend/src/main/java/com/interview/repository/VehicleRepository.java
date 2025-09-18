package com.interview.repository;

import com.interview.domain.Vehicle;
import com.interview.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleRepository extends
        JpaRepository<Vehicle, UUID>, QuerydslPredicateExecutor<Vehicle> {

    default Vehicle findByIdOrThrow(UUID id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Unable to find vehicle, id: " + id));
    }
}
