package com.interview.repository;

import com.interview.entity.TournamentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentTypeRepository extends JpaRepository<TournamentType, Long> {
}
