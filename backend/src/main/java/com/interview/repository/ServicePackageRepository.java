package com.interview.repository;

import com.interview.entity.ServicePackage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for ServicePackage entity operations.
 *
 * <p>Provides standard CRUD operations plus custom queries for finding
 * service packages with their customer relationships properly loaded.
 * Uses JOIN FETCH and EntityGraph to prevent N+1 queries and empty collections.
 */
@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {

    boolean existsByName(String name);

    @Query("SELECT DISTINCT sp FROM ServicePackage sp "
        + "LEFT JOIN FETCH sp.subscribers s "
        + "LEFT JOIN FETCH s.customerProfile "
        + "WHERE sp.id = :id")
    Optional<ServicePackage> findByIdWithSubscribers(@Param("id") Long id);

    @Query("SELECT DISTINCT sp FROM ServicePackage sp LEFT JOIN FETCH sp.subscribers s LEFT JOIN FETCH s.customerProfile WHERE sp.active = true")
    List<ServicePackage> findAllActiveWithSubscribers();

    @EntityGraph(attributePaths = {"subscribers", "subscribers.customerProfile"})
    @Query("SELECT sp FROM ServicePackage sp WHERE sp.active = true")
    Page<ServicePackage> findAllActiveWithSubscribers(Pageable pageable);

    @EntityGraph(attributePaths = {"subscribers", "subscribers.customerProfile"})
    @Query("SELECT sp FROM ServicePackage sp WHERE sp.active = false")
    Page<ServicePackage> findAllInactiveWithSubscribers(Pageable pageable);

    @Query("SELECT DISTINCT sp FROM ServicePackage sp LEFT JOIN FETCH sp.subscribers s LEFT JOIN FETCH s.customerProfile")
    List<ServicePackage> findAllWithSubscribers();

    @EntityGraph(attributePaths = {"subscribers", "subscribers.customerProfile"})
    @Query("SELECT sp FROM ServicePackage sp")
    Page<ServicePackage> findAllWithSubscribers(Pageable pageable);
}