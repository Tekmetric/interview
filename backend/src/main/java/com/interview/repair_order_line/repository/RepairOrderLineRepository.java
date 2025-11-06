package com.interview.repair_order_line.repository;

import com.interview.repair_order_line.domain.RepairOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairOrderLineRepository extends JpaRepository<RepairOrderLine, String> {

}
