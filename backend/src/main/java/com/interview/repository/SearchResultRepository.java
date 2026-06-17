package com.interview.repository;

import com.interview.entity.SearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchResultRepository extends JpaRepository<SearchResult, Long> {

    Page<SearchResult> findByNameContainingIgnoreCase(String query, Pageable pageable);

    Page<SearchResult> findByEntityTypeAndNameContainingIgnoreCase(String entityType, String query, Pageable pageable);

    Page<SearchResult> findByArtistNameContainingIgnoreCase(String artistName, Pageable pageable);
}
