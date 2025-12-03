package com.interview.repository;

import com.interview.domain.entity.PersonEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for performing CRUD operations on {@link PersonEntity}s.
 */
@Repository
public interface PersonRepository extends CrudRepository<PersonEntity, UUID>, PagingAndSortingRepository<PersonEntity, UUID> {
    @Query(value = "SELECT * FROM person WHERE email = ?1", nativeQuery = true)
    Optional<PersonEntity> findByEmail(String email);
}
