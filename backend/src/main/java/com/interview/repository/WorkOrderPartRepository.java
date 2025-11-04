package com.interview.repository;

import com.interview.entity.WorkOrderPartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderPartRepository extends JpaRepository<WorkOrderPartEntity, Long> {
}
