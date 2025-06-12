package com.interview.repository;

import com.interview.model.RepairOrderEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing RepairOrderEntity instances. Provides methods to find repair
 * orders by various criteria such as vehicle ID, status, description, customer ID, and date
 * ranges.
 */
@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrderEntity, Long> {

  List<RepairOrderEntity> findByVehicleId(Long vehicleId);

  List<RepairOrderEntity> findByStatus(String status);

  List<RepairOrderEntity> findByDescriptionContainingIgnoreCase(String description);

  @Query("SELECT r FROM RepairOrderEntity r WHERE r.vehicle.customer.id = :customerId")
  List<RepairOrderEntity> findByCustomerId(@Param("customerId") Long customerId);

  @Query("SELECT r FROM RepairOrderEntity r WHERE r.createdDate BETWEEN :startDate AND :endDate")
  List<RepairOrderEntity> findByCreatedDateBetween(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT r FROM RepairOrderEntity r WHERE r.updatedDate BETWEEN :startDate AND :endDate")
  List<RepairOrderEntity> findByUpdatedDateBetween(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT r FROM RepairOrderEntity r JOIN FETCH r.vehicle v JOIN FETCH v.customer WHERE r.id = :id")
  Optional<RepairOrderEntity> findByIdWithVehicleAndCustomer(@Param("id") Long id);

  @Query("SELECT r FROM RepairOrderEntity r WHERE r.vehicle.licensePlate = :licensePlate")
  List<RepairOrderEntity> findByVehicleLicensePlate(@Param("licensePlate") String licensePlate);

  @Query("SELECT r FROM RepairOrderEntity r WHERE r.status = :status AND r.createdDate >= :date")
  List<RepairOrderEntity> findByStatusAndCreatedDateAfter(@Param("status") String status,
      @Param("date") LocalDateTime date);

  @Query("SELECT COUNT(r) FROM RepairOrderEntity r WHERE r.vehicle.id = :vehicleId")
  long countByVehicleId(@Param("vehicleId") Long vehicleId);

  @Query("SELECT COUNT(r) FROM RepairOrderEntity r WHERE r.status = :status")
  long countByStatus(@Param("status") String status);

  @Query("SELECT COUNT(r) FROM RepairOrderEntity r WHERE r.vehicle.customer.id = :customerId")
  long countByCustomerId(@Param("customerId") Long customerId);

  // Common status queries
  @Query("SELECT r FROM RepairOrderEntity r WHERE r.status IN ('PENDING', 'IN_PROGRESS')")
  List<RepairOrderEntity> findActiveRepairOrders();

  @Query("SELECT r FROM RepairOrderEntity r WHERE r.status = 'COMPLETED' ORDER BY r.updatedDate DESC")
  List<RepairOrderEntity> findCompletedRepairOrdersOrderByUpdatedDate();

}
