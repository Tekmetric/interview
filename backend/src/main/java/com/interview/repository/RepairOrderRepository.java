package com.interview.repository;

import com.interview.model.RepairOrder;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {

  @Query(
      value = "SELECT ro FROM RepairOrder ro LEFT JOIN FETCH ro.car LEFT JOIN FETCH ro.mechanics",
      countQuery = "SELECT COUNT(ro) FROM RepairOrder ro")
  Page<RepairOrder> findAllWithDetails(Pageable pageable);

  @Query(
      "SELECT ro FROM RepairOrder ro LEFT JOIN FETCH ro.car LEFT JOIN FETCH ro.mechanics WHERE ro.id = :id")
  Optional<RepairOrder> findByIdWithDetails(Long id);
}
