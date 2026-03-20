package com.interview.repository;

import com.interview.domain.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
}
