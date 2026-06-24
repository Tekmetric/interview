package com.interview.repository;

import com.interview.entity.WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrderEntity, Long> {
}
