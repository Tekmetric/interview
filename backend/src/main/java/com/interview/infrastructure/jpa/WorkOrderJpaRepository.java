package com.interview.infrastructure.jpa;

import com.interview.infrastructure.jpa.WorkOrderEntity.WorkOrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderEntity, UUID> {

    List<WorkOrderEntity> findAllByStatus(WorkOrderStatusEntity status);

    boolean existsByVehicleId(UUID vehicleId);

    boolean existsByCustomerId(UUID customerId);
}
