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
}
