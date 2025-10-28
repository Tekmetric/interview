package com.interview.repository;

import com.interview.model.League;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The League Repository to do CRUD operations on the database extending {@link JpaRepository}
 */
public interface LeagueRepository extends JpaRepository<League, Long> {
    Optional<League> findByName(String name);
    boolean existsByNameAndLocationAndSkillLevel(String name, String location, String skillLevel);
}
