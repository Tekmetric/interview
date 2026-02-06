package com.interview.e2e;

import com.interview.base.BaseE2ETest;
import com.interview.dto.SongDto;
import com.interview.dto.SongListDto;
import com.interview.e2e.ArtistE2ETest.RestResponsePage;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SongE2ETest extends BaseE2ETest {

    @Test
    public void testCreateSong() {
        Artist artist = artistRepository.save(new Artist("Queen"));

        SongDto songDto = new SongDto(null, "Bohemian Rhapsody", 355L,
                LocalDate.of(1975, 10, 31), artist.getId(), null);

        webTestClient.post()
                .uri("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(songDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SongDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getTitle()).isEqualTo("Bohemian Rhapsody");
                    assertThat(response.getArtistId()).isEqualTo(artist.getId());
                    assertThat(response.getLengthInSeconds()).isEqualTo(355L);
                });

        // Verify in database
        assertThat(songRepository.findAll()).hasSize(1);
    }

    @Test
    public void testCreateSongWithAlbums() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Album album1 = albumRepository.save(new Album("Album 1", LocalDate.now(), artist));
        Album album2 = albumRepository.save(new Album("Album 2", LocalDate.now(), artist));

        SongDto songDto = new SongDto(null, "Test Song", 180L, LocalDate.now(),
                artist.getId(), Arrays.asList(album1.getId(), album2.getId()));

        webTestClient.post()
                .uri("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(songDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SongDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getAlbumIds()).hasSize(2);
                    assertThat(response.getAlbumIds()).containsExactlyInAnyOrder(album1.getId(), album2.getId());
                });
    }

    @Test
    public void testGetSong() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Song song = songRepository.save(new Song("Bohemian Rhapsody", 355L, LocalDate.now(), artist));

        webTestClient.get()
                .uri("/api/songs/{id}", song.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(SongDto.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(song.getId());
                    assertThat(response.getTitle()).isEqualTo("Bohemian Rhapsody");
                    assertThat(response.getArtistId()).isEqualTo(artist.getId());
                });
    }

    @Test
    public void testUpdateSong() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Song song = songRepository.save(new Song("Original Title", 180L, LocalDate.now(), artist));

        SongDto updateDto = new SongDto(song.getId(), "Updated Title", 200L,
                LocalDate.now(), artist.getId(), null);

        webTestClient.put()
                .uri("/api/songs/{id}", song.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SongDto.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(song.getId());
                    assertThat(response.getTitle()).isEqualTo("Updated Title");
                    assertThat(response.getLengthInSeconds()).isEqualTo(200L);
                });

        // Verify in database
        Song updated = songRepository.findById(song.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    public void testUpdateSongWithAlbumAssociations() {
        // Create artist and albums
        Artist artist = artistRepository.save(new Artist("Led Zeppelin"));
        Album album1 = albumRepository.save(new Album("Led Zeppelin IV", LocalDate.of(1971, 11, 8), artist));
        Album album2 = albumRepository.save(new Album("Physical Graffiti", LocalDate.of(1975, 2, 24), artist));
        Album album3 = albumRepository.save(new Album("Houses of the Holy", LocalDate.of(1973, 3, 28), artist));

        // Create song with initial albums
        Song song = songRepository.save(new Song("Kashmir", 515L, LocalDate.of(1975, 2, 24), artist));
        album1.addSong(song);
        album2.addSong(song);
        albumRepository.save(album1);
        albumRepository.save(album2);

        Long songId = song.getId();

        // Update song to change album associations (album1 and album3, removing album2)
        SongDto updateDto = new SongDto(
                songId,
                "Kashmir (Remastered)",
                520L,
                LocalDate.of(1975, 2, 24),
                artist.getId(),
                Arrays.asList(album1.getId(), album3.getId())
        );

        webTestClient.put()
                .uri("/api/songs/{id}", songId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SongDto.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(songId);
                    assertThat(response.getTitle()).isEqualTo("Kashmir (Remastered)");
                    assertThat(response.getAlbumIds()).hasSize(2);
                    assertThat(response.getAlbumIds()).containsExactlyInAnyOrder(album1.getId(), album3.getId());
                });

        // Verify by fetching again through the API
        webTestClient.get()
                .uri("/api/songs/{id}", songId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SongDto.class)
                .value(response -> {
                    assertThat(response.getTitle()).isEqualTo("Kashmir (Remastered)");
                    assertThat(response.getAlbumIds()).hasSize(2);
                    assertThat(response.getAlbumIds()).containsExactlyInAnyOrder(album1.getId(), album3.getId());
                });
    }

    @Test
    public void testDeleteSong() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Album album = albumRepository.save(new Album("Test Album", LocalDate.now(), artist));
        Song song = songRepository.save(new Song("To Delete", 180L, LocalDate.now(), artist));
        album.addSong(song);
        albumRepository.save(album);

        Long songId = song.getId();

        webTestClient.delete()
                .uri("/api/songs/{id}", songId)
                .exchange()
                .expectStatus().isNoContent();

        // Verify song deleted
        assertThat(songRepository.findById(songId)).isEmpty();

        // Verify album still exists
        assertThat(albumRepository.findById(album.getId())).isPresent();
    }

    @Test
    public void testGetAllSongs() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        songRepository.save(new Song("Song 1", 180L, LocalDate.now(), artist));
        songRepository.save(new Song("Song 2", 200L, LocalDate.now(), artist));
        songRepository.save(new Song("Song 3", 220L, LocalDate.now(), artist));

        webTestClient.get()
                .uri("/api/songs")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getTotalElements()).isEqualTo(3);
                    assertThat(page.getContent()).allMatch(song ->
                            song.getArtist().getName().equals("Queen")
                    );
                });
    }

    @Test
    public void testGetAllSongs_Pagination() {
        Artist artist = artistRepository.save(new Artist("The Beatles"));
        for (int i = 1; i <= 25; i++) {
            songRepository.save(new Song("Song " + i, 180L + i, LocalDate.now(), artist));
        }

        // First page
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
                    assertThat(page.getTotalElements()).isEqualTo(25);
                    assertThat(page.getTotalPages()).isEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                });

        // Second page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/songs")
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getNumber()).isEqualTo(1);
                });

        // Last page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/songs")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testGetAllSongs_Sorting() {
        Artist artist = artistRepository.save(new Artist("Various Artists"));
        songRepository.save(new Song("Zebra Song", 180L, LocalDate.of(2023, 1, 1), artist));
        songRepository.save(new Song("Alpha Song", 200L, LocalDate.of(2022, 1, 1), artist));
        songRepository.save(new Song("Beta Song", 220L, LocalDate.of(2021, 1, 1), artist));

        // Sort by title ascending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/songs")
                        .queryParam("sort", "title,asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Alpha Song");
                    assertThat(page.getContent().get(1).getTitle()).isEqualTo("Beta Song");
                    assertThat(page.getContent().get(2).getTitle()).isEqualTo("Zebra Song");
                });

        // Sort by title descending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/songs")
                        .queryParam("sort", "title,desc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Zebra Song");
                    assertThat(page.getContent().get(1).getTitle()).isEqualTo("Beta Song");
                    assertThat(page.getContent().get(2).getTitle()).isEqualTo("Alpha Song");
                });
    }

    @Test
    public void testGetAllSongs_EmptyResult() {
        webTestClient.get()
                .uri("/api/songs")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(0);
                });
    }

    @Test
    public void testSongNotFound() {
        webTestClient.get()
                .uri("/api/songs/{id}", 999999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testCreateSongValidation() {
        SongDto invalidDto = new SongDto(null, "", 180L, LocalDate.now(), null, null);

        webTestClient.post()
                .uri("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
