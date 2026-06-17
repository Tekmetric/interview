package com.interview.repository;

import com.interview.repository.model.RepairOrderEntity;
import com.interview.repository.model.WorkItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@Transactional(readOnly = true)
public interface WorkItemRepository extends JpaRepository<WorkItemEntity, Long> {

    Page<WorkItemEntity> findByRepairOrderEntityIdAndDeletedFalse(long repairOrderId, Pageable pageable);

    @Modifying
    @Query("UPDATE WorkItemEntity SET deleted = TRUE, updatedAt = :now WHERE id = :workItemId AND repairOrderEntity = :repairOrderEntity AND deleted = FALSE")
    int softDeleteByRepairOrderIdAndWorkItemId(@Param("repairOrderEntity") RepairOrderEntity repairOrderEntity, @Param("workItemId") long workItemId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE WorkItemEntity WHERE repairOrderEntity.id = :repairOrderId")
    int deleteAllByRepairOrderId(long repairOrderId);
}
