package com.interview.repository;

import com.interview.model.League;
import com.interview.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The Team Repository to do CRUD operations on the database extending {@link JpaRepository}
 */
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByName(String name);
    boolean existsByNameAndLeague(String name, League league);
}
