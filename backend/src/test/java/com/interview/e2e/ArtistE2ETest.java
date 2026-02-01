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
