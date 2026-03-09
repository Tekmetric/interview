package com.interview.repository;

import com.interview.repository.entity.WorkOrderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrderEntity, UUID> {

    @Query("SELECT w FROM work_order w LEFT JOIN FETCH w.partLineItems WHERE w.id = :id")
    Optional<WorkOrderEntity> findByIdWithPartLineItems(@Param("id") UUID id);

    @Query("SELECT w FROM work_order w LEFT JOIN FETCH w.laborLineItems WHERE w.id = :id")
    Optional<WorkOrderEntity> findByIdWithLaborLineItems(@Param("id") UUID id);
}
