package com.interview.repository;

import com.interview.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long>, JpaSpecificationExecutor<RepairOrder> {
    Optional<RepairOrder> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);

    @Query("SELECT COALESCE(SUM(li.lineTotal), 0) " +
            "FROM com.interview.entity.RepairLineItem li " +
            "WHERE li.repairOrder.id = :repairOrderId")
    BigDecimal computeSumForOrder(@Param("repairOrderId") Long repairOrderId);
}
