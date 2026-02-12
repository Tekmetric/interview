package com.interview.repository;

import com.interview.entity.CustomerProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for CustomerProfile entity operations.
 *
 * <p>Provides standard CRUD operations plus custom queries for finding
 * profiles by customer ID and email.
 */
@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

    @Query("SELECT cp FROM CustomerProfile cp JOIN cp.customer c WHERE c.email = :email")
    Optional<CustomerProfile> findByCustomerEmail(@Param("email") String email);
}
