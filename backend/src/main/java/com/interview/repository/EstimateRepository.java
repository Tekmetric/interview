package com.interview.repository;

import com.interview.entity.Estimate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstimateRepository extends JpaRepository<Estimate, UUID>, JpaSpecificationExecutor<Estimate> {
    @Query(value = "SELECT CAST(work_order_id AS VARCHAR) FROM estimate_work_orders WHERE estimate_id = :estimateId", nativeQuery = true)
    List<String> findWorkOrderIdsByEstimateId(@Param("estimateId") UUID estimateId);
}
