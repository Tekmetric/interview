package com.interview.repositories;

import com.interview.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// TODO: EXPLAIN CrudRepository vs JpaRepository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    long countByLastName(String firstName);
}
