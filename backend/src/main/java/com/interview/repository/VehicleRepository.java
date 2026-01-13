package com.interview.repository;

import com.interview.model.User;
import com.interview.model.Vehicle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    
    @Override
    @EntityGraph(attributePaths = {"owner"})
    Optional<Vehicle> findById(Long id);
    
    @EntityGraph(attributePaths = {"owner"})
    List<Vehicle> findByOwner(User owner);
}
