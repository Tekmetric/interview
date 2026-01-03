package com.interview.repository;

import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import static com.interview.specification.RepairJobSpecifications.queryAll;

@Repository
public interface RepairJobRepository extends JpaRepository<RepairJob, Long>, JpaSpecificationExecutor<RepairJob> {

    default Page<RepairJob> findRepairJob(String userId, RepairStatus status, String licensePlate, Pageable pageable) {
        return findAll(queryAll(userId, status, licensePlate), pageable);
    }
}