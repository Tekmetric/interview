package com.interview.repair_order.repository;

import com.interview.repair_order.domain.RepairOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, String> {

    @EntityGraph(attributePaths = "repairOrderLines")
    @Query("Select ro from RepairOrder ro")
    Page<RepairOrder> findAllWithLinesPageable(Pageable pageable);
}
