package com.interview.repository;

import com.interview.base.BaseRepositoryTest;
import com.interview.entity.Artist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class ArtistRepositoryIntegrationTest extends BaseRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    public void testSaveArtist() {
        Artist artist = new Artist("New Artist");
        Artist saved = artistRepository.save(artist);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Artist");
    }

    @Test
    public void testFindById() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);
        flushAndClear();

        Artist found = artistRepository.findById(artist.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test Artist");
    }

    @Test
    public void testFindAll() {
        for (int i = 1; i <= 10; i++) {
            entityManager.persist(new Artist("Artist " + i));
        }
        flushAndClear();

        assertThat(artistRepository.findAll()).hasSize(10);
    }

    @Test
    public void testFindByNameContainingIgnoreCase() {
        Artist artist1 = new Artist("Queen");
        Artist artist2 = new Artist("The Beatles");
        Artist artist3 = new Artist("The Rolling Stones");

        artistRepository.save(artist1);
        artistRepository.save(artist2);
        artistRepository.save(artist3);

        Page<Artist> results = artistRepository.findByNameContainingIgnoreCase("the", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent()).extracting(Artist::getName)
            .containsExactlyInAnyOrder("The Beatles", "The Rolling Stones");
    }

    @Test
    public void testFindByNameContainingIgnoreCaseWithPagination() {
        // Create 25 artists with "Band" in name
        for (int i = 1; i <= 25; i++) {
            artistRepository.save(new Artist("Band " + i));
        }

        // First page
        Page<Artist> page0 = artistRepository.findByNameContainingIgnoreCase("band", PageRequest.of(0, 10));
        assertThat(page0.getContent()).hasSize(10);
        assertThat(page0.getTotalElements()).isEqualTo(25);
        assertThat(page0.getTotalPages()).isEqualTo(3);

        // Second page
        Page<Artist> page1 = artistRepository.findByNameContainingIgnoreCase("band", PageRequest.of(1, 10));
        assertThat(page1.getContent()).hasSize(10);
        assertThat(page1.getNumber()).isEqualTo(1);
    }

    @Test
    public void testExistsByName() {
        artistRepository.save(new Artist("Queen"));

        assertThat(artistRepository.existsByName("Queen")).isTrue();
        assertThat(artistRepository.existsByName("NonExistent")).isFalse();
    }

    @Test
    public void testDeleteArtist() {
        Artist artist = new Artist("To Delete");
        Artist saved = artistRepository.save(artist);
        Long id = saved.getId();

        artistRepository.deleteById(id);
        flushAndClear();

        assertThat(artistRepository.findById(id)).isEmpty();
    }
}
