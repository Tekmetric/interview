package com.interview.repository;

import com.interview.model.CustomerEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing CustomerEntity instances. Provides methods to find customers by
 * email and ID, and check existence by email.
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

  Optional<CustomerEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  @Query("SELECT c FROM CustomerEntity c LEFT JOIN FETCH c.vehicles WHERE c.id = :id")
  Optional<CustomerEntity> findByIdWithVehicles(@Param("id") Long id);

  List<CustomerEntity> findAllByOrderById();
}
