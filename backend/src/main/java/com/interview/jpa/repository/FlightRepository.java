package com.interview.jpa.repository;

import com.interview.jpa.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface FlightRepository extends JpaRepository<Flight, Integer>, JpaSpecificationExecutor<Flight> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Integer id);

    @Query("""
           SELECT f
           FROM Flight f
           LEFT JOIN FETCH f.plane
           LEFT JOIN FETCH f.createdBy
           LEFT JOIN FETCH f.updatedBy
           WHERE f.id = :id
           """)
    Flight findByIdWithFetch(Integer id);

    /** Overlap exists if existing.dep < newArr AND existing.arr > newDep (back-to-back is OK). */
    @Query("""
            SELECT (count(f) > 0) from Flight f
            WHERE f.plane.id = :planeId
              AND f.departureTime < :newArrival
              AND f.arrivalTime   > :newDeparture
            """)
    boolean existsOverlap(Integer planeId, LocalDateTime newDeparture, LocalDateTime newArrival);

    @Query("""
            select (count(f) > 0) from Flight f
            where f.plane.id = :planeId
              and f.id <> :excludeId
              and f.departureTime < :newArrival
              and f.arrivalTime   > :newDeparture
            """)
    boolean existsOverlapExcluding(Integer planeId, Integer excludeId,
                                   LocalDateTime newDeparture, LocalDateTime newArrival);
}
