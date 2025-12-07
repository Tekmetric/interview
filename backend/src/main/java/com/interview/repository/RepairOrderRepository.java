package com.interview.repository;

import com.interview.repository.model.RepairOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface RepairOrderRepository extends JpaRepository<RepairOrderEntity, Long> {

    @Modifying
    @Query("DELETE FROM RepairOrderEntity WHERE id = :id")
    int deleteRepairOrderById(@Param("id") long id);
}
