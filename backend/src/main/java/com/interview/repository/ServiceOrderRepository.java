package com.interview.repository;

import com.interview.model.entity.ServiceOrder;
import com.interview.model.enums.ServiceOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    Page<ServiceOrder> findAllByVehicles_Vin(String vin, Pageable pageable);
    Page<ServiceOrder> findAllByVehicles_VinAndStatus(String vin, ServiceOrderStatus status, Pageable pageable);
}
