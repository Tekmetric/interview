package com.interview.repository;

import com.interview.entity.WorkOrder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID>, JpaSpecificationExecutor<WorkOrder> {
    @Query("""
        SELECT DISTINCT workOrder
        FROM Estimate estimate
        JOIN estimate.workOrders workOrder
        LEFT JOIN FETCH workOrder.partsNeeded workOrderPart
        LEFT JOIN FETCH workOrderPart.part
        WHERE estimate.id = :estimateId
        """)
    List<WorkOrder> findAvailableForEstimateResponse(@Param("estimateId") UUID estimateId);
}
