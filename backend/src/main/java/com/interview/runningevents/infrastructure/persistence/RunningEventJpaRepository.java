package com.interview.runningevents.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for RunningEventEntity.
 */
@Repository
public interface RunningEventJpaRepository extends JpaRepository<RunningEventEntity, Long> {

    /**
     * Find running events within a date range.
     *
     * @param fromDate The minimum date (inclusive)
     * @param toDate The maximum date (inclusive)
     * @param pageable Pagination information
     * @return A page of running events within the date range
     */
    @Query("SELECT e FROM RunningEventEntity e WHERE " + "(:fromDate IS NULL OR e.dateTime >= :fromDate) AND "
            + "(:toDate IS NULL OR e.dateTime <= :toDate)")
    Page<RunningEventEntity> findByDateRange(
            @Param("fromDate") Long fromDate, @Param("toDate") Long toDate, Pageable pageable);
}
