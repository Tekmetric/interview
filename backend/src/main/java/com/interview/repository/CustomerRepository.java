package com.interview.repository;

import com.interview.entity.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Customer entity operations.
 *
 * <p>Provides standard CRUD operations plus custom queries for finding
 * customers by email and fetching customers with their profiles.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.customerProfile WHERE c.id = :id")
    Optional<Customer> findByIdWithProfile(@Param("id") Long id);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.customerProfile LEFT JOIN FETCH c.subscribedPackages WHERE c.id = :id")
    Optional<Customer> findByIdWithSubscriptions(@Param("id") Long id);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.customerProfile")
    List<Customer> findAllWithProfiles();

    @Query(value = "SELECT c FROM Customer c LEFT JOIN FETCH c.customerProfile",
           countQuery = "SELECT count(c) FROM Customer c")
    Page<Customer> findAllWithProfiles(Pageable pageable);

    @Modifying
    @Query("DELETE FROM Customer c WHERE c.id = :id")
    int deleteByCustomerId(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Customer c "
        + "LEFT JOIN FETCH c.customerProfile "
        + "LEFT JOIN FETCH c.subscribedPackages "
        + "WHERE c.id = :id")
    Optional<Customer> findByIdWithProfileAndServicePackages(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Customer c "
        + "LEFT JOIN FETCH c.customerProfile "
        + "LEFT JOIN FETCH c.subscribedPackages")
    List<Customer> findAllWithProfilesAndServicePackages();

    @EntityGraph(attributePaths = {"customerProfile", "servicePackages"})
    Page<Customer> findAll(Pageable pageable);
}
