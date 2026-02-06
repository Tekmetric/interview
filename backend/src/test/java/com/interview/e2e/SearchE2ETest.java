package com.interview.e2e;

import com.interview.base.BaseE2ETest;
import com.interview.dto.SearchResultDto;
import com.interview.e2e.ArtistE2ETest.RestResponsePage;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchE2ETest extends BaseE2ETest {

    @Test
    public void testSearchAllEntities() {
        // Create artist, song, and album with "Queen" in the name
        Artist artist = artistRepository.save(new Artist("Queen"));
        songRepository.save(new Song("Queen of Hearts", 180L, LocalDate.now(), artist));
        albumRepository.save(new Album("Queen's Greatest Hits", LocalDate.now(), artist));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Queen")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSizeGreaterThanOrEqualTo(3);

                    // Verify we have results of all three types
                    boolean hasArtist = page.getContent().stream()
                            .anyMatch(r -> "ARTIST".equals(r.getEntityType()) && "Queen".equals(r.getName()));
                    boolean hasSong = page.getContent().stream()
                            .anyMatch(r -> "SONG".equals(r.getEntityType()) && "Queen of Hearts".equals(r.getName()));
                    boolean hasAlbum = page.getContent().stream()
                            .anyMatch(r -> "ALBUM".equals(r.getEntityType()) && "Queen's Greatest Hits".equals(r.getName()));

                    assertThat(hasArtist).isTrue();
                    assertThat(hasSong).isTrue();
                    assertThat(hasAlbum).isTrue();
                });
    }

    @Test
    public void testSearchCaseInsensitive() {
        Artist artist = artistRepository.save(new Artist("Queen"));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "QUEEN")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSizeGreaterThanOrEqualTo(1);
                    assertThat(page.getContent()).anyMatch(r -> "Queen".equals(r.getName()));
                });
    }

    @Test
    public void testSearchByEntityType() {
        Artist artist = artistRepository.save(new Artist("Music Artist"));
        songRepository.save(new Song("Music Song", 180L, LocalDate.now(), artist));
        albumRepository.save(new Album("Music Album", LocalDate.now(), artist));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "music")
                        .queryParam("type", "SONG")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isNotEmpty();
                    assertThat(page.getContent()).allMatch(r -> "SONG".equals(r.getEntityType()));
                    assertThat(page.getContent()).anyMatch(r -> "Music Song".equals(r.getName()));
                });
    }

    @Test
    public void testSearchByArtistName() {
        Artist queen = artistRepository.save(new Artist("Queen"));
        Artist beatles = artistRepository.save(new Artist("The Beatles"));

        songRepository.save(new Song("Bohemian Rhapsody", 355L, LocalDate.now(), queen));
        songRepository.save(new Song("Hey Jude", 431L, LocalDate.now(), beatles));
        albumRepository.save(new Album("A Night at the Opera", LocalDate.now(), queen));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("artist", "Queen")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isNotEmpty();
                    assertThat(page.getContent()).allMatch(r -> r.getArtistName().contains("Queen"));
                });
    }

    @Test
    public void testSearchSubstringMatching() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        songRepository.save(new Song("Bohemian Rhapsody", 355L, LocalDate.now(), artist));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "rhapsody")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isNotEmpty();
                    assertThat(page.getContent()).anyMatch(r ->
                            "Bohemian Rhapsody".equals(r.getName())
                    );
                });
    }

    @Test
    public void testSearchNoResults() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "nonexistent")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                    assertThat(page.getTotalElements()).isEqualTo(0);
                });
    }

    @Test
    public void testSearchResultFormat() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Song song = songRepository.save(new Song("Bohemian Rhapsody", 355L,
                LocalDate.of(1975, 10, 31), artist));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Bohemian")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    SearchResultDto result = page.getContent().stream()
                            .filter(r -> "SONG".equals(r.getEntityType()))
                            .findFirst()
                            .orElseThrow();

                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getEntityType()).isEqualTo("SONG");
                    assertThat(result.getName()).isEqualTo("Bohemian Rhapsody");
                    assertThat(result.getArtistName()).isEqualTo("Queen");
                    assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(1975, 10, 31));
                });
    }

    @Test
    public void testSearchEmptyQuery() {
        Artist artist = artistRepository.save(new Artist("Queen"));

        webTestClient.get()
                .uri("/api/search")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                });
    }

    @Test
    public void testSearchPagination_GeneralQuery() {
        // Create 25 items that match "Music"
        Artist artist = artistRepository.save(new Artist("Music Lover"));

        for (int i = 1; i <= 12; i++) {
            songRepository.save(new Song("Music Song " + i, 180L, LocalDate.now(), artist));
        }
        for (int i = 1; i <= 12; i++) {
            albumRepository.save(new Album("Music Album " + i, LocalDate.now(), artist));
        }

        // First page with size 10
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Music")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(25);
                    assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                    assertThat(page.getSize()).isEqualTo(10);
                });

        // Second page with size 10
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Music")
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getNumber()).isEqualTo(1);
                });

        // Third page with size 10
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Music")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSizeGreaterThanOrEqualTo(5);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testSearchPagination_ByType() {
        // Create 15 songs that match "Rock"
        Artist artist = artistRepository.save(new Artist("Rock Band"));

        for (int i = 1; i <= 15; i++) {
            songRepository.save(new Song("Rock Song " + i, 180L, LocalDate.now(), artist));
        }

        // First page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Rock")
                        .queryParam("type", "SONG")
                        .queryParam("page", 0)
                        .queryParam("size", 5)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(15);
                    assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                    assertThat(page.getSize()).isEqualTo(5);
                    // All results should be songs
                    assertThat(page.getContent()).allMatch(r -> "SONG".equals(r.getEntityType()));
                });

        // Second page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Rock")
                        .queryParam("type", "SONG")
                        .queryParam("page", 1)
                        .queryParam("size", 5)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getNumber()).isEqualTo(1);
                });
    }

    @Test
    public void testSearchPagination_ByArtist() {
        // Create artist with multiple songs and albums
        Artist beatles = artistRepository.save(new Artist("The Beatles"));

        for (int i = 1; i <= 10; i++) {
            songRepository.save(new Song("Beatles Song " + i, 180L, LocalDate.now(), beatles));
        }
        for (int i = 1; i <= 10; i++) {
            albumRepository.save(new Album("Beatles Album " + i, LocalDate.now(), beatles));
        }

        // First page with size 8
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("artist", "Beatles")
                        .queryParam("page", 0)
                        .queryParam("size", 8)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(8);
                    assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(20);
                    assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                    assertThat(page.getSize()).isEqualTo(8);
                    // All results should be from The Beatles
                    assertThat(page.getContent()).allMatch(r -> r.getArtistName().contains("Beatles"));
                });

        // Last page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("artist", "Beatles")
                        .queryParam("page", 2)
                        .queryParam("size", 8)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSizeGreaterThanOrEqualTo(4);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testSearchPagination_DifferentPageSizes() {
        // Create 30 items
        Artist artist = artistRepository.save(new Artist("Jazz Musician"));

        for (int i = 1; i <= 30; i++) {
            songRepository.save(new Song("Jazz Track " + i, 180L, LocalDate.now(), artist));
        }

        // Test with size 5
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Jazz")
                        .queryParam("page", 0)
                        .queryParam("size", 5)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getSize()).isEqualTo(5);
                    assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(6);
                });

        // Test with size 15
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Jazz")
                        .queryParam("page", 0)
                        .queryParam("size", 15)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(15);
                    assertThat(page.getSize()).isEqualTo(15);
                    assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(2);
                });

        // Test with size 50 (should get all results on one page)
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Jazz")
                        .queryParam("page", 0)
                        .queryParam("size", 50)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSizeGreaterThanOrEqualTo(30);
                    assertThat(page.getSize()).isEqualTo(50);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                });
    }

    @Test
    public void testSearchPagination_EmptyPage() {
        // Create only 5 items
        Artist artist = artistRepository.save(new Artist("Solo Artist"));

        for (int i = 1; i <= 5; i++) {
            songRepository.save(new Song("Solo Song " + i, 180L, LocalDate.now(), artist));
        }

        // Request page 2 with size 10 (should be empty since we only have 5 items)
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("q", "Solo")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SearchResultDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                    assertThat(page.getNumber()).isEqualTo(2);
                    assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(5);
                });
    }
}
