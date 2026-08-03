package com.interview.repository;

import com.interview.model.RepairOrder;
import com.interview.model.RepairOrderStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, UUID> {

  @Query("SELECT ro FROM RepairOrder ro LEFT JOIN FETCH ro.lineItems WHERE ro.id = :id")
  Optional<RepairOrder> findByIdWithLineItems(UUID id);

  Page<RepairOrder> findByCustomerId(UUID customerId, Pageable pageable);

  Page<RepairOrder> findByStatus(RepairOrderStatus status, Pageable pageable);
}
