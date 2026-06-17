package com.interview.repository;

import com.interview.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    Page<Song> findByArtistId(Long artistId, Pageable pageable);

    Page<Song> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Song> findByAlbumsId(Long albumId, Pageable pageable);
}
