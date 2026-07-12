package com.interview.repository;

import com.interview.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    // Find event by ID with venue and performers eagerly fetched
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.venue LEFT JOIN FETCH e.performers WHERE e.id = :id")
    Optional<Event> findByIdWithDetails(@Param("id") UUID id);

    // Find events by name (case-insensitive)
    List<Event> findByNameContainingIgnoreCase(String name);

    // Find events by location
    List<Event> findByLocation(String location);

    // Find events after a specific date
    List<Event> findByEventDateAfter(LocalDateTime date);

    // Find events between two dates
    List<Event> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Cursor-based pagination: get first page ordered by createdAt, id
    @Query("SELECT e FROM Event e ORDER BY e.createdAt ASC, e.id ASC")
    List<Event> findFirstPage(Pageable pageable);

    // Cursor-based pagination: get events after cursor (simplified - uses only createdAt)
    @Query("SELECT e FROM Event e WHERE e.createdAt > :cursorCreatedAt ORDER BY e.createdAt ASC, e.id ASC")
    List<Event> findEventsAfterCursor(@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
                                      Pageable pageable);
}