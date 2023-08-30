package com.interview.repository;

import com.interview.entity.Racquet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository class for Racquet Entity
 */
@Repository
public interface RacquetRepository extends JpaRepository<Racquet, Long> {
}
