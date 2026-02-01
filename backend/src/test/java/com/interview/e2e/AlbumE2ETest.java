package com.interview.e2e;

import com.interview.base.BaseE2ETest;
import com.interview.dto.AlbumDto;
import com.interview.dto.AlbumListDto;
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

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumE2ETest extends BaseE2ETest {

    @Test
    public void testCreateAlbum() {
        Artist artist = artistRepository.save(new Artist("Queen"));

        AlbumDto albumDto = new AlbumDto(null, "A Night at the Opera",
                LocalDate.of(1975, 11, 21), artist.getId(), null);

        webTestClient.post()
                .uri("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(albumDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AlbumDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getTitle()).isEqualTo("A Night at the Opera");
                    assertThat(response.getArtistId()).isEqualTo(artist.getId());
                    assertThat(response.getReleaseDate()).isEqualTo(LocalDate.of(1975, 11, 21));
                });

        // Verify in database
        assertThat(albumRepository.findAll()).hasSize(1);
    }

    @Test
    public void testCreateAlbumWithSongs() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Song song1 = songRepository.save(new Song("Song 1", 180L, LocalDate.now(), artist));
        Song song2 = songRepository.save(new Song("Song 2", 200L, LocalDate.now(), artist));

        AlbumDto albumDto = new AlbumDto(null, "Test Album", LocalDate.now(),
                artist.getId(), Arrays.asList(song1.getId(), song2.getId()));

        webTestClient.post()
                .uri("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(albumDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AlbumDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getSongIds()).hasSize(2);
                    assertThat(response.getSongIds()).containsExactlyInAnyOrder(song1.getId(), song2.getId());
                });
    }

    @Test
    public void testGetAlbum() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Album album = albumRepository.save(new Album("A Night at the Opera", LocalDate.now(), artist));

        webTestClient.get()
                .uri("/api/albums/{id}", album.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AlbumDto.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(album.getId());
                    assertThat(response.getTitle()).isEqualTo("A Night at the Opera");
                    assertThat(response.getArtistId()).isEqualTo(artist.getId());
                });
    }

    @Test
    public void testUpdateAlbum() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Album album = albumRepository.save(new Album("Original Title", LocalDate.now(), artist));

        AlbumDto updateDto = new AlbumDto(album.getId(), "Updated Title",
                LocalDate.of(2020, 1, 1), artist.getId(), null);

        webTestClient.put()
                .uri("/api/albums/{id}", album.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AlbumDto.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(album.getId());
                    assertThat(response.getTitle()).isEqualTo("Updated Title");
                    assertThat(response.getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
                });

        // Verify in database
        Album updated = albumRepository.findById(album.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    public void testUpdateAlbumWithSongAssociations() {
        // Create artist and songs
        Artist artist = artistRepository.save(new Artist("Pink Floyd"));
        Song song1 = songRepository.save(new Song("Comfortably Numb", 382L, LocalDate.of(1979, 11, 30), artist));
        Song song2 = songRepository.save(new Song("Another Brick in the Wall", 240L, LocalDate.of(1979, 11, 30), artist));
        Song song3 = songRepository.save(new Song("Hey You", 280L, LocalDate.of(1979, 11, 30), artist));

        // Create album with initial songs
        Album album = albumRepository.save(new Album("The Wall", LocalDate.of(1979, 11, 30), artist));
        album.addSong(song1);
        album.addSong(song2);
        albumRepository.save(album);

        Long albumId = album.getId();

        // Update album to change song associations (song1 and song3, removing song2)
        AlbumDto updateDto = new AlbumDto(
                albumId,
                "The Wall (Remastered)",
                LocalDate.of(1979, 11, 30),
                artist.getId(),
                Arrays.asList(song1.getId(), song3.getId())
        );

        webTestClient.put()
                .uri("/api/albums/{id}", albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AlbumDto.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(albumId);
                    assertThat(response.getTitle()).isEqualTo("The Wall (Remastered)");
                    assertThat(response.getSongIds()).hasSize(2);
                    assertThat(response.getSongIds()).containsExactlyInAnyOrder(song1.getId(), song3.getId());
                });

        // Verify by fetching again through the API
        webTestClient.get()
                .uri("/api/albums/{id}", albumId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AlbumDto.class)
                .value(response -> {
                    assertThat(response.getTitle()).isEqualTo("The Wall (Remastered)");
                    assertThat(response.getSongIds()).hasSize(2);
                    assertThat(response.getSongIds()).containsExactlyInAnyOrder(song1.getId(), song3.getId());
                });
    }

    @Test
    public void testDeleteAlbum() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Song song = songRepository.save(new Song("Test Song", 180L, LocalDate.now(), artist));
        Album album = albumRepository.save(new Album("To Delete", LocalDate.now(), artist));
        album.addSong(song);
        albumRepository.save(album);

        Long albumId = album.getId();

        webTestClient.delete()
                .uri("/api/albums/{id}", albumId)
                .exchange()
                .expectStatus().isNoContent();

        // Verify album deleted
        assertThat(albumRepository.findById(albumId)).isEmpty();

        // Verify songs still exist
        assertThat(songRepository.findById(song.getId())).isPresent();
    }

    @Test
    public void testGetAllAlbums() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        albumRepository.save(new Album("Album 1", LocalDate.now(), artist));
        albumRepository.save(new Album("Album 2", LocalDate.now(), artist));
        albumRepository.save(new Album("Album 3", LocalDate.now(), artist));

        webTestClient.get()
                .uri("/api/albums")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getTotalElements()).isEqualTo(3);
                    assertThat(page.getContent()).allMatch(album ->
                            album.getArtist().getName().equals("Queen")
                    );
                });
    }

    @Test
    public void testGetAlbumSongs() {
        Artist artist = artistRepository.save(new Artist("Queen"));
        Song song1 = songRepository.save(new Song("Song 1", 180L, LocalDate.now(), artist));
        Song song2 = songRepository.save(new Song("Song 2", 200L, LocalDate.now(), artist));

        Album album = albumRepository.save(new Album("Test Album", LocalDate.now(), artist));
        album.addSong(song1);
        album.addSong(song2);
        albumRepository.save(album);

        webTestClient.get()
                .uri("/api/albums/{id}/songs", album.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(2);
                    assertThat(page.getContent()).extracting("title")
                            .containsExactlyInAnyOrder("Song 1", "Song 2");
                });
    }

    @Test
    public void testAlbumNotFound() {
        webTestClient.get()
                .uri("/api/albums/{id}", 999999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testCreateAlbumValidation() {
        AlbumDto invalidDto = new AlbumDto(null, "", LocalDate.now(), null, null);

        webTestClient.post()
                .uri("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
