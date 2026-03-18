package com.interview.repository;

import com.interview.model.ServiceJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ServiceJobRepository extends JpaRepository<ServiceJob, Long> {

    Page<ServiceJob> findByVehicleId(Long vehicleId, Pageable pageable);

    @Query(value = "select sj from ServiceJob sj where sj.vehicle.customer.id = :customerId",
        countQuery = "select count(sj) from ServiceJob sj where sj.vehicle.customer.id = :customerId")
    Page<ServiceJob> findByVehicleCustomerId(Long customerId, Pageable pageable);
}
