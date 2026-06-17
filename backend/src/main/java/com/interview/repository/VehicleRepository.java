package com.interview.repository;

import com.interview.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Vehicle entity operations.
 *
 * <p>Provides standard CRUD operations plus custom queries for finding
 * vehicles by customer, VIN, and fetching vehicles with their customer data.
 * Supports JPA Specifications for complex filtering with @EntityGraph for performance.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    boolean existsByVin(String vin);

    @Query("SELECT v FROM Vehicle v LEFT JOIN FETCH v.customer c LEFT JOIN FETCH c.customerProfile WHERE v.id = :id")
    Optional<Vehicle> findByIdWithCustomer(@Param("id") Long id);

    @Query("SELECT v FROM Vehicle v LEFT JOIN FETCH v.customer c LEFT JOIN FETCH c.customerProfile")
    List<Vehicle> findAllWithCustomers();

    @Query(value = "SELECT v FROM Vehicle v LEFT JOIN FETCH v.customer c LEFT JOIN FETCH c.customerProfile",
           countQuery = "SELECT count(v) FROM Vehicle v")
    Page<Vehicle> findAllWithCustomers(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "customer.customerProfile"})
    Page<Vehicle> findAll(Specification<Vehicle> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "customer.customerProfile"})
    List<Vehicle> findAll(Specification<Vehicle> spec);

    @Modifying
    @Query("DELETE FROM Vehicle v WHERE v.id = :id")
    int deleteByVehicleId(@Param("id") Long id);
}