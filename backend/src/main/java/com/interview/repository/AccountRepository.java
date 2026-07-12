package com.interview.repository;

import com.interview.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    // Find account by email (includes deleted accounts)
    Optional<Account> findByEmail(String email);

    // Find account by email excluding deleted accounts
    Optional<Account> findByEmailAndDeletedAtIsNull(String email);

    // Find all non-deleted accounts
    List<Account> findByDeletedAtIsNull();

    // Find all non-deleted accounts with pagination
    Page<Account> findByDeletedAtIsNull(Pageable pageable);

    // Find account by ID excluding deleted accounts
    Optional<Account> findByIdAndDeletedAtIsNull(UUID id);

    // Find accounts by first name or last name (case-insensitive), excluding deleted accounts
    @Query("SELECT a FROM Account a WHERE (LOWER(a.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND a.deletedAt IS NULL")
    List<Account> findByNameContainingAndNotDeleted(String firstName, String lastName);

    // Original method - includes deleted accounts
    List<Account> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}