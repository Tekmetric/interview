package com.interview.workorder.dao;

import com.interview.workorder.entity.WorkOrder;
import com.interview.workorder.model.WorkOrderStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    @EntityGraph(attributePaths = "customer")
    Page<WorkOrder> findAllByCustomer_Id(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = "customer")
    Page<WorkOrder> findAllByCustomer_IdAndStatus(Long customerId, WorkOrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "customer")
    Optional<WorkOrder> findByIdAndCustomer_Id(Long id, Long customerId);
}
