package com.interview.repository;

import com.interview.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The Team Repository to do CRUD operations on the database extending {@link JpaRepository}
 */
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
}
