package com.interview.repository;

import com.interview.repository.entity.VehicleEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID> {
    Page<VehicleEntity> findAllByCustomerId(UUID customerId, Pageable pageable);
}
