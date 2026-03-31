package com.interview.repository;

import com.interview.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for repair order persistence and simple existence checks.
 */
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    boolean existsByVehicleVin(String vin);

    boolean existsByVehicleVinAndIdNot(String vin, Long id);
}
