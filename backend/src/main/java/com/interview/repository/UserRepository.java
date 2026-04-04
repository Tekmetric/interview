package com.interview.repository;

import com.interview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides CRUD operations for User entitiesnby default
 * Additional methods are given for future use cases.
 * Spring Data JPA will automatically implement this interface.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // --- Derived Query Methods (Spring generates SQL automatically) ---

    // Find by exact name
    Optional<List<User>> findByName(String name);


    // --- Custom JPQL Queries ---

    // Find user by email using JPQL
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);


    // --- Native SQL Query ---

    // Find users by name using native SQL
    @Query(value = "SELECT * FROM users WHERE name = :name", nativeQuery = true)
    List<User> findUsersByNameNative(@Param("name") String name);
}