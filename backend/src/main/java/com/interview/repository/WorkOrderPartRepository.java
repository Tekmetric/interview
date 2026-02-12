package com.interview.repository;

import com.interview.entity.WorkOrderEntity;
import com.interview.entity.WorkOrderPartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkOrderPartRepository extends JpaRepository<WorkOrderPartEntity, Long> {
    @Query("SELECT wop.workOrder FROM WorkOrderPartEntity wop WHERE wop.part.id = :partId")
    List<WorkOrderEntity> findWorkOrdersByPartId(Long partId);
}
