package com.interview.repository;

import com.interview.base.BaseRepositoryTest;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumRepositoryIntegrationTest extends BaseRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    public void testFindByArtistId() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        for (int i = 1; i <= 5; i++) {
            Album album = new Album("Album " + i, LocalDate.now(), artist);
            entityManager.persist(album);
        }
        flushAndClear();

        Page<Album> albums = albumRepository.findByArtistId(artist.getId(), PageRequest.of(0, 10));

        assertThat(albums.getContent()).hasSize(5);
        assertThat(albums.getContent()).allMatch(a -> a.getArtist().getId().equals(artist.getId()));
    }

    @Test
    public void testFindByArtistIdWithPagination() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        // Create 25 albums
        for (int i = 1; i <= 25; i++) {
            Album album = new Album("Album " + i, LocalDate.now(), artist);
            entityManager.persist(album);
        }
        flushAndClear();

        // First page
        Page<Album> page0 = albumRepository.findByArtistId(artist.getId(), PageRequest.of(0, 10));
        assertThat(page0.getContent()).hasSize(10);
        assertThat(page0.getTotalElements()).isEqualTo(25);

        // Verify pagination metadata
        assertThat(page0.getTotalPages()).isEqualTo(3);
    }

    @Test
    public void testFindByTitleContainingIgnoreCase() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Album album1 = new Album("A Night at the Opera", LocalDate.now(), artist);
        Album album2 = new Album("The Wall", LocalDate.now(), artist);
        Album album3 = new Album("Another Album", LocalDate.now(), artist);

        entityManager.persist(album1);
        entityManager.persist(album2);
        entityManager.persist(album3);
        flushAndClear();

        Page<Album> results = albumRepository.findByTitleContainingIgnoreCase("opera", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getTitle()).isEqualTo("A Night at the Opera");
    }
}
