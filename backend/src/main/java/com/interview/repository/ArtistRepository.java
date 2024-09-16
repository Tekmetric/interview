package com.interview.repository;

import com.interview.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("select distinct a from Artist a")
    List<Artist> findAllDistinctArtists();

}
