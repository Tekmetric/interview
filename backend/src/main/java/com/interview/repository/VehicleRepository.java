package com.interview.repository;

import com.interview.entity.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    
    Optional<VehicleEntity> findByVin(String vin);
    
    List<VehicleEntity> findByCustomerId(Long customerId);
    
    boolean existsByVin(String vin);
    
    Page<VehicleEntity> findByVinContainingIgnoreCase(String vin, Pageable pageable);
}
