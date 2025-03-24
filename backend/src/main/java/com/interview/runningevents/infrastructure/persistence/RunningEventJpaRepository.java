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
     * Find running events within a date range ordered by date and time.
     *
     * @param startDate The minimum date (inclusive)
     * @param endDate The maximum date (inclusive)
     * @param pageable Pagination information
     * @return A page of running events within the date range
     */
    Page<RunningEventEntity> findByDateTimeBetweenOrderByDateTime(Long startDate, Long endDate, Pageable pageable);

    /**
     * Find all running events ordered by date and time.
     *
     * @param pageable Pagination information
     * @return A page of running events
     */
    Page<RunningEventEntity> findAllByOrderByDateTime(Pageable pageable);
}
