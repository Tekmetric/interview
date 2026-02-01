package com.interview.e2e;

import com.interview.base.BaseE2ETest;
import com.interview.dto.AlbumListDto;
import com.interview.dto.ArtistListDto;
import com.interview.dto.SearchResultDto;
import com.interview.dto.SongListDto;
import com.interview.e2e.ArtistE2ETest.RestResponsePage;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class PaginationE2ETest extends BaseE2ETest {

    @Test
    public void testArtistsPagination() {
        // Create 50 artists
        for (int i = 1; i <= 50; i++) {
            artistRepository.save(new Artist("Artist " + String.format("%02d", i)));
        }

        // First page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("page", 0)
                        .queryParam("size", 20)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(20);
                    assertThat(page.getTotalElements()).isEqualTo(50);
                    assertThat(page.getTotalPages()).isEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                });

        // Second page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("page", 1)
                        .queryParam("size", 20)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(20);
                    assertThat(page.getNumber()).isEqualTo(1);
                });

        // Third page (partial)
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("page", 2)
                        .queryParam("size", 20)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testSongsPagination() {
        Artist artist = artistRepository.save(new Artist("Test Artist"));

        // Create 30 songs
        for (int i = 1; i <= 30; i++) {
            songRepository.save(new Song("Song " + String.format("%02d", i), 180L, LocalDate.now(), artist));
        }

        // Test with different page sizes
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/songs")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getTotalElements()).isEqualTo(30);
                    assertThat(page.getTotalPages()).isEqualTo(3);
                });
    }

    @Test
    public void testAlbumsPagination() {
        Artist artist = artistRepository.save(new Artist("Test Artist"));

        // Create 25 albums
        for (int i = 1; i <= 25; i++) {
            albumRepository.save(new Album("Album " + String.format("%02d", i), LocalDate.now(), artist));
        }

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/albums")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getTotalElements()).isEqualTo(25);
                    assertThat(page.getTotalPages()).isEqualTo(3);
                });
    }

    @Test
    public void testSongsByArtistPagination() {
        Artist artist = artistRepository.save(new Artist("Test Artist"));
        final Long artistId = artist.getId();

        // Create 40 songs
        for (int i = 1; i <= 40; i++) {
            songRepository.save(new Song("Song " + i, 180L, LocalDate.now(), artist));
        }

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists/{id}/songs")
                        .queryParam("page", 0)
                        .queryParam("size", 15)
                        .build(artistId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(15);
                    assertThat(page.getTotalElements()).isEqualTo(40);
                });
    }

    @Test
    public void testSearchPagination() {
        Artist artist = artistRepository.save(new Artist("Test Artist"));

        // Create 50 entities with "Test" in name
        for (int i = 1; i <= 50; i++) {
            songRepository.save(new Song("Test Song " + i, 180L, LocalDate.now(), artist));
        }

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Test")
                        .queryParam("page", 0)
                        .queryParam("size", 20)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSizeLessThanOrEqualTo(20);
                    assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(50);
                });
    }

    @Test
    public void testSortingWithPagination() {
        artistRepository.save(new Artist("Zebra"));
        artistRepository.save(new Artist("Apple"));
        artistRepository.save(new Artist("Mango"));

        // Test ascending sort (default)
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .queryParam("sort", "name,asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent().get(0).getName()).isEqualTo("Apple");
                    assertThat(page.getContent().get(1).getName()).isEqualTo("Mango");
                    assertThat(page.getContent().get(2).getName()).isEqualTo("Zebra");
                });

        // Test descending sort
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .queryParam("sort", "name,desc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent().get(0).getName()).isEqualTo("Zebra");
                    assertThat(page.getContent().get(1).getName()).isEqualTo("Mango");
                    assertThat(page.getContent().get(2).getName()).isEqualTo("Apple");
                });
    }

    @Test
    public void testDefaultPagination() {
        Artist artist = artistRepository.save(new Artist("Test Artist"));

        // Create 25 songs
        for (int i = 1; i <= 25; i++) {
            songRepository.save(new Song("Song " + i, 180L, LocalDate.now(), artist));
        }

        // Test default pagination (should be 20 per page)
        webTestClient.get()
                .uri("/api/songs")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getSize()).isEqualTo(20);
                    assertThat(page.getContent()).hasSize(20);
                });
    }
}
