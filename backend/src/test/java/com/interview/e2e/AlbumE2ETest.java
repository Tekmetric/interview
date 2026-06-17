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
    public void testGetAllAlbums_Pagination() {
        Artist artist = artistRepository.save(new Artist("The Beatles"));
        for (int i = 1; i <= 25; i++) {
            albumRepository.save(new Album("Album " + i, LocalDate.now(), artist));
        }

        // First page
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
                    assertThat(page.getNumber()).isEqualTo(0);
                });

        // Second page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/albums")
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getNumber()).isEqualTo(1);
                });

        // Last page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/albums")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testGetAllAlbums_Sorting() {
        Artist artist = artistRepository.save(new Artist("Various Artists"));
        albumRepository.save(new Album("Zebra Album", LocalDate.of(2023, 1, 1), artist));
        albumRepository.save(new Album("Alpha Album", LocalDate.of(2022, 1, 1), artist));
        albumRepository.save(new Album("Beta Album", LocalDate.of(2021, 1, 1), artist));

        // Sort by title ascending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/albums")
                        .queryParam("sort", "title,asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Alpha Album");
                    assertThat(page.getContent().get(1).getTitle()).isEqualTo("Beta Album");
                    assertThat(page.getContent().get(2).getTitle()).isEqualTo("Zebra Album");
                });

        // Sort by title descending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/albums")
                        .queryParam("sort", "title,desc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Zebra Album");
                    assertThat(page.getContent().get(1).getTitle()).isEqualTo("Beta Album");
                    assertThat(page.getContent().get(2).getTitle()).isEqualTo("Alpha Album");
                });
    }

    @Test
    public void testGetAllAlbums_MultipleArtists() {
        Artist artist1 = artistRepository.save(new Artist("The Beatles"));
        Artist artist2 = artistRepository.save(new Artist("Queen"));
        Artist artist3 = artistRepository.save(new Artist("Pink Floyd"));

        albumRepository.save(new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist1));
        albumRepository.save(new Album("Let It Be", LocalDate.of(1970, 5, 8), artist1));
        albumRepository.save(new Album("A Night at the Opera", LocalDate.of(1975, 11, 21), artist2));
        albumRepository.save(new Album("The Wall", LocalDate.of(1979, 11, 30), artist3));

        webTestClient.get()
                .uri("/api/albums")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(4);
                    assertThat(page.getTotalElements()).isEqualTo(4);

                    // Verify artist references are included
                    assertThat(page.getContent()).extracting(album -> album.getArtist().getName())
                            .containsExactlyInAnyOrder("The Beatles", "The Beatles", "Queen", "Pink Floyd");
                });
    }

    @Test
    public void testGetAllAlbums_WithSongCounts() {
        Artist artist = artistRepository.save(new Artist("Test Artist"));
        Song song1 = songRepository.save(new Song("Song 1", 180L, LocalDate.now(), artist));
        Song song2 = songRepository.save(new Song("Song 2", 200L, LocalDate.now(), artist));
        Song song3 = songRepository.save(new Song("Song 3", 220L, LocalDate.now(), artist));

        Album album1 = albumRepository.save(new Album("Album with 2 songs", LocalDate.now(), artist));
        album1.addSong(song1);
        album1.addSong(song2);
        albumRepository.save(album1);

        Album album2 = albumRepository.save(new Album("Album with 1 song", LocalDate.now(), artist));
        album2.addSong(song3);
        albumRepository.save(album2);

        Album album3 = albumRepository.save(new Album("Album with no songs", LocalDate.now(), artist));

        webTestClient.get()
                .uri("/api/albums")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);

                    AlbumListDto albumWith2Songs = page.getContent().stream()
                            .filter(a -> a.getTitle().equals("Album with 2 songs"))
                            .findFirst().orElseThrow();
                    assertThat(albumWith2Songs.getSongCount()).isEqualTo(2);
                    assertThat(albumWith2Songs.getSongs()).hasSize(2);

                    AlbumListDto albumWith1Song = page.getContent().stream()
                            .filter(a -> a.getTitle().equals("Album with 1 song"))
                            .findFirst().orElseThrow();
                    assertThat(albumWith1Song.getSongCount()).isEqualTo(1);

                    AlbumListDto albumWithNoSongs = page.getContent().stream()
                            .filter(a -> a.getTitle().equals("Album with no songs"))
                            .findFirst().orElseThrow();
                    assertThat(albumWithNoSongs.getSongCount()).isEqualTo(0);
                });
    }

    @Test
    public void testGetAllAlbums_EmptyResult() {
        webTestClient.get()
                .uri("/api/albums")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<AlbumListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(0);
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
    public void testGetAlbumSongs_Pagination() {
        Artist artist = artistRepository.save(new Artist("The Rolling Stones"));
        Album album = albumRepository.save(new Album("Greatest Hits", LocalDate.now(), artist));

        for (int i = 1; i <= 25; i++) {
            Song song = songRepository.save(new Song("Song " + i, 180L + i, LocalDate.now(), artist));
            album.addSong(song);
        }
        albumRepository.save(album);

        // First page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/albums/{id}/songs")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(album.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(10);
                    assertThat(page.getTotalElements()).isEqualTo(25);
                    assertThat(page.getTotalPages()).isEqualTo(3);
                    assertThat(page.getNumber()).isEqualTo(0);
                });

        // Last page
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/albums/{id}/songs")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build(album.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(5);
                    assertThat(page.getNumber()).isEqualTo(2);
                });
    }

    @Test
    public void testGetAlbumSongs_Sorting() {
        Artist artist = artistRepository.save(new Artist("David Bowie"));
        Album album = albumRepository.save(new Album("The Rise and Fall of Ziggy Stardust", LocalDate.of(1972, 6, 16), artist));

        Song song1 = songRepository.save(new Song("Ziggy Stardust", 193L, LocalDate.of(1972, 6, 16), artist));
        Song song2 = songRepository.save(new Song("Five Years", 283L, LocalDate.of(1972, 6, 16), artist));
        Song song3 = songRepository.save(new Song("Starman", 256L, LocalDate.of(1972, 6, 16), artist));

        album.addSong(song1);
        album.addSong(song2);
        album.addSong(song3);
        albumRepository.save(album);

        // Sort by title ascending
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/albums/{id}/songs")
                        .queryParam("sort", "title,asc")
                        .build(album.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Five Years");
                    assertThat(page.getContent().get(1).getTitle()).isEqualTo("Starman");
                    assertThat(page.getContent().get(2).getTitle()).isEqualTo("Ziggy Stardust");
                });
    }

    @Test
    public void testGetAlbumSongs_EmptyResult() {
        Artist artist = artistRepository.save(new Artist("New Artist"));
        Album album = albumRepository.save(new Album("Empty Album", LocalDate.now(), artist));

        webTestClient.get()
                .uri("/api/albums/{id}/songs", album.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<SongListDto>>() {})
                .value(page -> {
                    assertThat(page.getContent()).isEmpty();
                    assertThat(page.getTotalElements()).isEqualTo(0);
                });
    }

    @Test
    public void testGetAlbumSongs_NotFound() {
        webTestClient.get()
                .uri("/api/albums/{id}/songs", 999999L)
                .exchange()
                .expectStatus().isNotFound();
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
