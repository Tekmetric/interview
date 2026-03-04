package com.interview.workorder.dao;

import com.interview.workorder.entity.WorkOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    @EntityGraph(attributePaths = "customer")
    List<WorkOrder> findAllByCustomer_Id(Long customerId, Sort sort);

    @EntityGraph(attributePaths = "customer")
    Optional<WorkOrder> findByIdAndCustomer_Id(Long id, Long customerId);
}
