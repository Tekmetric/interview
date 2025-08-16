package com.interview.repositories;

import com.interview.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// TODO: EXPLAIN CrudRepository vs JpaRepository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    long countByLastName(String firstName);

    Optional<Customer> findByEmail(String email);

    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.addresses")
    List<Customer> findAllWithAddresses();

    // Use EntityGraph to load customer and associated addresses in one join query to avoid N+1 problem
    @Override
    @EntityGraph(attributePaths = "addresses")
    @NonNull
    Page<Customer> findAll(@NonNull Pageable pageable);

    // EntityGraph with Specifications to avoid N+1 problem
    @Override
    @EntityGraph(attributePaths = "addresses")
    @NonNull
    Page<Customer> findAll(Specification<Customer> spec, @NonNull Pageable pageable);
}