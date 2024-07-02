package com.interview.repository;

import com.interview.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository class for Tournament Entity
 */
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
