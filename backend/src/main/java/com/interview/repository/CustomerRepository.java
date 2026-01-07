package com.interview.repository;

import com.interview.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
    Optional<CustomerEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Page<CustomerEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    
    Page<CustomerEntity> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    
    Page<CustomerEntity> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
}
