package com.interview.repository;

import com.interview.base.BaseRepositoryTest;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class SongRepositoryIntegrationTest extends BaseRepositoryTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    public void testFindByArtistId() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        for (int i = 1; i <= 5; i++) {
            Song song = new Song("Song " + i, 180L, LocalDate.now(), artist);
            entityManager.persist(song);
        }
        flushAndClear();

        Page<Song> songs = songRepository.findByArtistId(artist.getId(), PageRequest.of(0, 10));

        assertThat(songs.getContent()).hasSize(5);
        assertThat(songs.getContent()).allMatch(s -> s.getArtist().getId().equals(artist.getId()));
    }

    @Test
    public void testFindByArtistIdWithPagination() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        // Create 30 songs
        for (int i = 1; i <= 30; i++) {
            Song song = new Song("Song " + i, 180L, LocalDate.now(), artist);
            entityManager.persist(song);
        }
        flushAndClear();

        // First page
        Page<Song> page0 = songRepository.findByArtistId(artist.getId(), PageRequest.of(0, 10));
        assertThat(page0.getContent()).hasSize(10);
        assertThat(page0.getTotalElements()).isEqualTo(30);
    }

    @Test
    public void testFindByTitleContainingIgnoreCase() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Song song1 = new Song("Bohemian Rhapsody", 355L, LocalDate.now(), artist);
        Song song2 = new Song("We Are The Champions", 179L, LocalDate.now(), artist);
        Song song3 = new Song("Another Song", 200L, LocalDate.now(), artist);

        entityManager.persist(song1);
        entityManager.persist(song2);
        entityManager.persist(song3);
        flushAndClear();

        Page<Song> results = songRepository.findByTitleContainingIgnoreCase("rhapsody", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getTitle()).isEqualTo("Bohemian Rhapsody");
    }

    @Test
    public void testFindByAlbumsId() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Song song1 = new Song("Song 1", 180L, LocalDate.now(), artist);
        Song song2 = new Song("Song 2", 200L, LocalDate.now(), artist);
        Song song3 = new Song("Song 3", 220L, LocalDate.now(), artist);

        entityManager.persist(song1);
        entityManager.persist(song2);
        entityManager.persist(song3);

        Album album = new Album("Test Album", LocalDate.now(), artist);
        album.addSong(song1);
        album.addSong(song2);
        entityManager.persist(album);

        flushAndClear();

        Page<Song> songs = songRepository.findByAlbumsId(album.getId(), PageRequest.of(0, 10));

        assertThat(songs.getContent()).hasSize(2);
        assertThat(songs.getContent()).extracting(Song::getTitle)
            .containsExactlyInAnyOrder("Song 1", "Song 2");
    }

    @Test
    public void testFindByAlbumsIdWithPagination() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Album album = new Album("Test Album", LocalDate.now(), artist);

        // Create 20 songs on the album
        for (int i = 1; i <= 20; i++) {
            Song song = new Song("Song " + i, 180L, LocalDate.now(), artist);
            entityManager.persist(song);
            album.addSong(song);
        }
        entityManager.persist(album);
        flushAndClear();

        Page<Song> page0 = songRepository.findByAlbumsId(album.getId(), PageRequest.of(0, 10));
        assertThat(page0.getContent()).hasSize(10);
        assertThat(page0.getTotalElements()).isEqualTo(20);
    }

    @Test
    public void testSongWithMultipleAlbums() {
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Song song = new Song("Multi-Album Song", 180L, LocalDate.now(), artist);
        Album album1 = new Album("Album 1", LocalDate.now(), artist);
        Album album2 = new Album("Album 2", LocalDate.now(), artist);
        Album album3 = new Album("Album 3", LocalDate.now(), artist);

        album1.addSong(song);
        album2.addSong(song);
        album3.addSong(song);

        entityManager.persist(song);
        entityManager.persist(album1);
        entityManager.persist(album2);
        entityManager.persist(album3);
        flushAndClear();

        // Query from each album
        Page<Song> fromAlbum1 = songRepository.findByAlbumsId(album1.getId(), PageRequest.of(0, 10));
        Page<Song> fromAlbum2 = songRepository.findByAlbumsId(album2.getId(), PageRequest.of(0, 10));
        Page<Song> fromAlbum3 = songRepository.findByAlbumsId(album3.getId(), PageRequest.of(0, 10));

        // Verify song appears in all 3 album queries
        assertThat(fromAlbum1.getContent()).hasSize(1);
        assertThat(fromAlbum2.getContent()).hasSize(1);
        assertThat(fromAlbum3.getContent()).hasSize(1);
        assertThat(fromAlbum1.getContent().get(0).getId()).isEqualTo(song.getId());
    }
}
