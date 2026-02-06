package com.interview.e2e;

import com.interview.base.BaseE2ETest;
import com.interview.dto.AlbumListDto;
import com.interview.dto.ArtistDto;
import com.interview.dto.ArtistListDto;
import com.interview.dto.SongListDto;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ArtistE2ETest extends BaseE2ETest {

    @Test
    public void testCreateArtist() {
        ArtistDto artistDto = new ArtistDto(null, "Queen");

        webTestClient.post()
                .uri("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(artistDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ArtistDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo("Queen");
                });

        // Verify in database
        assertThat(artistRepository.findAll()).hasSize(1);
    }

    @Test
    public void testGetArtist() {
        // Create artist via repository
        Artist artist = new Artist("The Beatles");
        artist = artistRepository.save(artist);
        final Long artistId = artist.getId();

        webTestClient.get()
                .uri("/api/artists/{id}", artistId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArtistDto.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(artistId);
                    assertThat(response.getName()).isEqualTo("The Beatles");
                });
    }

    @Test
    public void testUpdateArtist() {
        Artist artist = new Artist("Original Name");
        artist = artistRepository.save(artist);
        final Long artistId = artist.getId();

        ArtistDto updateDto = new ArtistDto(artistId, "Updated Name");

        webTestClient.put()
                .uri("/api/artists/{id}", artistId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArtistDto.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(artistId);
                    assertThat(response.getName()).isEqualTo("Updated Name");
                });

        // Verify in database
        Artist updated = artistRepository.findById(artistId).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    public void testDeleteArtist() {
        Artist artist = new Artist("To Delete");
        artist = artistRepository.save(artist);
        Long artistId = artist.getId();

        webTestClient.delete()
                .uri("/api/artists/{id}", artistId)
                .exchange()
                .expectStatus().isNoContent();

        // Verify removed from database
        assertThat(artistRepository.findById(artistId)).isEmpty();
    }

    @Test
    public void testGetAllArtists() {
        // Create multiple artists
        artistRepository.save(new Artist("Artist 1"));
        artistRepository.save(new Artist("Artist 2"));
        artistRepository.save(new Artist("Artist 3"));

        webTestClient.get()
                .uri("/api/artists")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getTotalElements()).isEqualTo(3);
                });
    }

    @Test
    public void testGetAllArtists_Pagination() {
        // Create 25 artists for pagination testing
        for (int i = 1; i <= 25; i++) {
            artistRepository.save(new Artist("Artist " + i));
        }

        // First page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getTotalElements()).isEqualTo(25);
                    assertThat(page.getTotalPages()).isEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                });

        // Second page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getNumber()).isEqualTo(1);
                });

        // Third page (only 5 items)
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testGetAllArtists_Sorting() {
        artistRepository.save(new Artist("Zebra Band"));
        artistRepository.save(new Artist("Alpha Band"));
        artistRepository.save(new Artist("Beta Band"));

        // Sort by name ascending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("sort", "name,asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getName()).isEqualTo("Alpha Band");
                    assertThat(page.getContent().get(1).getName()).isEqualTo("Beta Band");
                    assertThat(page.getContent().get(2).getName()).isEqualTo("Zebra Band");
                });

        // Sort by name descending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("sort", "name,desc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getName()).isEqualTo("Zebra Band");
                    assertThat(page.getContent().get(1).getName()).isEqualTo("Beta Band");
                    assertThat(page.getContent().get(2).getName()).isEqualTo("Alpha Band");
                });
    }

    @Test
    public void testGetAllArtists_WithAlbumAndSongCounts() {
        // Create artists with different numbers of albums and songs
        Artist artist1 = artistRepository.save(new Artist("Queen"));
        Artist artist2 = artistRepository.save(new Artist("The Beatles"));
        Artist artist3 = artistRepository.save(new Artist("Pink Floyd"));

        // Queen: 2 albums, 3 songs
        albumRepository.save(new Album("A Night at the Opera", LocalDate.of(1975, 11, 21), artist1));
        albumRepository.save(new Album("News of the World", LocalDate.of(1977, 10, 28), artist1));
        songRepository.save(new Song("Bohemian Rhapsody", 355L, LocalDate.of(1975, 10, 31), artist1));
        songRepository.save(new Song("We Will Rock You", 122L, LocalDate.of(1977, 10, 7), artist1));
        songRepository.save(new Song("We Are the Champions", 179L, LocalDate.of(1977, 10, 7), artist1));

        // The Beatles: 1 album, 2 songs
        albumRepository.save(new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist2));
        songRepository.save(new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist2));
        songRepository.save(new Song("Something", 182L, LocalDate.of(1969, 9, 26), artist2));

        // Pink Floyd: 1 album, 1 song
        albumRepository.save(new Album("The Wall", LocalDate.of(1979, 11, 30), artist3));
        songRepository.save(new Song("Another Brick in the Wall", 238L, LocalDate.of(1979, 11, 23), artist3));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists")
                        .queryParam("sort", "name,asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);

                    // Pink Floyd
                    ArtistListDto pinkFloyd = page.getContent().get(0);
                    assertThat(pinkFloyd.getName()).isEqualTo("Pink Floyd");
                    assertThat(pinkFloyd.getAlbumCount()).isEqualTo(1);
                    assertThat(pinkFloyd.getSongCount()).isEqualTo(1);

                    // Queen
                    ArtistListDto queen = page.getContent().get(1);
                    assertThat(queen.getName()).isEqualTo("Queen");
                    assertThat(queen.getAlbumCount()).isEqualTo(2);
                    assertThat(queen.getSongCount()).isEqualTo(3);

                    // The Beatles
                    ArtistListDto beatles = page.getContent().get(2);
                    assertThat(beatles.getName()).isEqualTo("The Beatles");
                    assertThat(beatles.getAlbumCount()).isEqualTo(1);
                    assertThat(beatles.getSongCount()).isEqualTo(2);
                });
    }

    @Test
    public void testGetAllArtists_EmptyResult() {
        // Don't create any artists
        webTestClient.get()
                .uri("/api/artists")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ArtistListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(0);
                });
    }

    @Test
    public void testCascadeDelete() {
        // Create artist with songs and albums
        Artist artist = new Artist("Queen");
        artistRepository.save(artist);

        Song song1 = new Song("Bohemian Rhapsody", 355L, LocalDate.now(), artist);
        Song song2 = new Song("We Will Rock You", 122L, LocalDate.now(), artist);
        songRepository.save(song1);
        songRepository.save(song2);

        Album album1 = new Album("A Night at the Opera", LocalDate.now(), artist);
        Album album2 = new Album("News of the World", LocalDate.now(), artist);
        albumRepository.save(album1);
        albumRepository.save(album2);

        Long artistId = artist.getId();

        // Delete artist
        webTestClient.delete()
                .uri("/api/artists/{id}", artistId)
                .exchange()
                .expectStatus().isNoContent();

        // Verify artist deleted
        assertThat(artistRepository.findById(artistId)).isEmpty();

        // Verify all associated songs deleted
        assertThat(songRepository.findAll()).isEmpty();

        // Verify all associated albums deleted
        assertThat(albumRepository.findAll()).isEmpty();
    }

    @Test
    public void testGetArtistSongs() {
        Artist artist = new Artist("Queen");
        artistRepository.save(artist);

        songRepository.save(new Song("Song 1", 180L, LocalDate.now(), artist));
        songRepository.save(new Song("Song 2", 200L, LocalDate.now(), artist));
        songRepository.save(new Song("Song 3", 220L, LocalDate.now(), artist));

        webTestClient.get()
                .uri("/api/artists/{id}/songs", artist.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent()).allMatch(song ->
                            song.getArtist().getId().equals(artist.getId()) &&
                                    song.getArtist().getName().equals("Queen")
                    );
                });
    }

    @Test
    public void testGetArtistSongs_Pagination() {
        Artist artist = artistRepository.save(new Artist("The Beatles"));

        for (int i = 1; i <= 25; i++) {
            songRepository.save(new Song("Beatles Song " + i, 180L + i, LocalDate.now(), artist));
        }

        // First page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists/{id}/songs")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(artist.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getTotalElements()).isEqualTo(25);
                    assertThat(page.getTotalPages()).isEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                    assertThat(page.getContent()).allMatch(song ->
                            song.getArtist().getName().equals("The Beatles")
                    );
                });

        // Last page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists/{id}/songs")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build(artist.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testGetArtistSongs_Sorting() {
        Artist artist = artistRepository.save(new Artist("Pink Floyd"));
        songRepository.save(new Song("Wish You Were Here", 180L, LocalDate.of(1975, 9, 15), artist));
        songRepository.save(new Song("Another Brick in the Wall", 240L, LocalDate.of(1979, 11, 23), artist));
        songRepository.save(new Song("Comfortably Numb", 380L, LocalDate.of(1979, 11, 30), artist));

        // Sort by title ascending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists/{id}/songs")
                        .queryParam("sort", "title,asc")
                        .build(artist.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Another Brick in the Wall");
                    assertThat(page.getContent().get(1).getTitle()).isEqualTo("Comfortably Numb");
                    assertThat(page.getContent().get(2).getTitle()).isEqualTo("Wish You Were Here");
                });
    }

    @Test
    public void testGetArtistSongs_EmptyResult() {
        Artist artist = artistRepository.save(new Artist("New Artist"));

        webTestClient.get()
                .uri("/api/artists/{id}/songs", artist.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                    assertThat(page.getTotalElements()).isEqualTo(0);
                });
    }

    @Test
    public void testGetArtistSongs_NotFound() {
        webTestClient.get()
                .uri("/api/artists/{id}/songs", 999999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testGetArtistAlbums() {
        Artist artist = new Artist("Queen");
        artistRepository.save(artist);

        albumRepository.save(new Album("Album 1", LocalDate.now(), artist));
        albumRepository.save(new Album("Album 2", LocalDate.now(), artist));

        webTestClient.get()
                .uri("/api/artists/{id}/albums", artist.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(2);
                    assertThat(page.getContent()).allMatch(album ->
                            album.getArtist().getId().equals(artist.getId()) &&
                                    album.getArtist().getName().equals("Queen")
                    );
                });
    }

    @Test
    public void testGetArtistAlbums_Pagination() {
        Artist artist = artistRepository.save(new Artist("Led Zeppelin"));

        for (int i = 1; i <= 25; i++) {
            albumRepository.save(new Album("Album " + i, LocalDate.now().minusDays(i), artist));
        }

        // First page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists/{id}/albums")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(artist.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getTotalElements()).isEqualTo(25);
                    assertThat(page.getTotalPages()).isEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                    assertThat(page.getContent()).allMatch(album ->
                            album.getArtist().getName().equals("Led Zeppelin")
                    );
                });

        // Last page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists/{id}/albums")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build(artist.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testGetArtistAlbums_Sorting() {
        Artist artist = artistRepository.save(new Artist("The Who"));
        albumRepository.save(new Album("Who's Next", LocalDate.of(1971, 8, 14), artist));
        albumRepository.save(new Album("Tommy", LocalDate.of(1969, 5, 23), artist));
        albumRepository.save(new Album("Quadrophenia", LocalDate.of(1973, 10, 19), artist));

        // Sort by title ascending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/artists/{id}/albums")
                        .queryParam("sort", "title,asc")
                        .build(artist.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Quadrophenia");
                    assertThat(page.getContent().get(1).getTitle()).isEqualTo("Tommy");
                    assertThat(page.getContent().get(2).getTitle()).isEqualTo("Who's Next");
                });
    }

    @Test
    public void testGetArtistAlbums_EmptyResult() {
        Artist artist = artistRepository.save(new Artist("New Artist"));

        webTestClient.get()
                .uri("/api/artists/{id}/albums", artist.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                    assertThat(page.getTotalElements()).isEqualTo(0);
                });
    }

    @Test
    public void testGetArtistAlbums_NotFound() {
        webTestClient.get()
                .uri("/api/artists/{id}/albums", 999999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testArtistNotFound() {
        webTestClient.get()
                .uri("/api/artists/{id}", 999999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testCreateArtistValidation() {
        ArtistDto invalidDto = new ArtistDto(null, "");

        webTestClient.post()
                .uri("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // Helper class for deserializing pageable responses
    static class RestResponsePage<T> {
        private List<T> content;
        private int totalPages;
        private long totalElements;
        private int size;
        private int number;

        public List<T> getContent() {
            return content;
        }

        public void setContent(List<T> content) {
            this.content = content;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
