package com.interview.repository;

import com.interview.model.VehicleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing VehicleEntity instances. Provides methods to find vehicles by
 * license plate, customer ID, and various fetch strategies for related entities.
 */
@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

  Optional<VehicleEntity> findByLicensePlate(String licensePlate);

  List<VehicleEntity> findByCustomerId(Long customerId);

  boolean existsByLicensePlate(String licensePlate);

  @Query("SELECT v FROM VehicleEntity v JOIN FETCH v.customer WHERE v.id = :id")
  Optional<VehicleEntity> findByIdWithCustomer(@Param("id") Long id);

  @Query("SELECT v FROM VehicleEntity v JOIN FETCH v.customer LEFT JOIN FETCH v.repairOrders WHERE v.id = :id")
  Optional<VehicleEntity> findByIdWithCustomerAndRepairOrders(@Param("id") Long id);

  @Query("SELECT COUNT(v) FROM VehicleEntity v WHERE v.customer.id = :customerId")
  long countByCustomerId(@Param("customerId") Long customerId);

}
