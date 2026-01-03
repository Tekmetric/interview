package com.interview.repository;

import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairJobRepository extends JpaRepository<RepairJob, Long>, JpaSpecificationExecutor<RepairJob> {
    List<RepairJob> findByUserId(String userId);
    List<RepairJob> findByStatus(RepairStatus status);
}