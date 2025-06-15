package com.interview.runningevents.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for RunningEventEntity.
 */
@Repository
public interface RunningEventJpaRepository extends JpaRepository<RunningEventEntity, Long> {

    /**
     * Find running events within a date range.
     * The order is specified in the Pageable parameter.
     *
     * @param startDate The minimum date (inclusive)
     * @param endDate The maximum date (inclusive)
     * @param pageable Pagination and sort information
     * @return A page of running events within the date range
     */
    Page<RunningEventEntity> findByDateTimeBetween(Long startDate, Long endDate, Pageable pageable);
}
