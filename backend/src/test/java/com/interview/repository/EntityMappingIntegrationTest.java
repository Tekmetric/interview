package com.interview.repository;

import com.interview.base.BaseRepositoryTest;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class EntityMappingIntegrationTest extends BaseRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    public void testArtistEntityPersistence() {
        // Create and persist Artist
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);
        flushAndClear();

        // Retrieve and verify
        Artist found = entityManager.find(Artist.class, artist.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test Artist");
        assertThat(found.getId()).isNotNull();
    }

    @Test
    public void testSongEntityPersistence() {
        // Create Artist and Song
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Song song = new Song("Test Song", 180L, LocalDate.of(2020, 1, 1), artist);
        entityManager.persist(song);
        flushAndClear();

        // Retrieve and verify
        Song found = entityManager.find(Song.class, song.getId());
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Test Song");
        assertThat(found.getLengthInSeconds()).isEqualTo(180L);
        assertThat(found.getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(found.getArtist()).isNotNull();
        assertThat(found.getArtist().getId()).isEqualTo(artist.getId());
    }

    @Test
    public void testAlbumEntityPersistence() {
        // Create Artist and Album
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Album album = new Album("Test Album", LocalDate.of(2020, 1, 1), artist);
        entityManager.persist(album);
        flushAndClear();

        // Retrieve and verify
        Album found = entityManager.find(Album.class, album.getId());
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Test Album");
        assertThat(found.getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(found.getArtist()).isNotNull();
    }

    @Test
    public void testArtistToSongsOneToManyRelationship() {
        // Create Artist with multiple Songs
        Artist artist = new Artist("Test Artist");
        Song song1 = new Song("Song 1", 180L, LocalDate.now(), artist);
        Song song2 = new Song("Song 2", 200L, LocalDate.now(), artist);
        Song song3 = new Song("Song 3", 220L, LocalDate.now(), artist);

        artist.addSong(song1);
        artist.addSong(song2);
        artist.addSong(song3);

        entityManager.persist(artist);
        entityManager.persist(song1);
        entityManager.persist(song2);
        entityManager.persist(song3);
        flushAndClear();

        // Retrieve and verify
        Artist found = entityManager.find(Artist.class, artist.getId());
        assertThat(found.getSongs()).hasSize(3);
        assertThat(found.getSongs()).extracting(Song::getTitle)
            .containsExactlyInAnyOrder("Song 1", "Song 2", "Song 3");

        // Verify bidirectional relationship
        assertThat(found.getSongs().get(0).getArtist().getId()).isEqualTo(artist.getId());
    }

    @Test
    public void testArtistToAlbumsOneToManyRelationship() {
        // Create Artist with multiple Albums
        Artist artist = new Artist("Test Artist");
        Album album1 = new Album("Album 1", LocalDate.now(), artist);
        Album album2 = new Album("Album 2", LocalDate.now(), artist);

        artist.addAlbum(album1);
        artist.addAlbum(album2);

        entityManager.persist(artist);
        entityManager.persist(album1);
        entityManager.persist(album2);
        flushAndClear();

        // Retrieve and verify
        Artist found = entityManager.find(Artist.class, artist.getId());
        assertThat(found.getAlbums()).hasSize(2);
        assertThat(found.getAlbums()).extracting(Album::getTitle)
            .containsExactlyInAnyOrder("Album 1", "Album 2");

        // Verify bidirectional relationship
        assertThat(found.getAlbums().get(0).getArtist().getId()).isEqualTo(artist.getId());
    }

    @Test
    public void testSongToAlbumsManyToManyRelationship() {
        // Create Song and Albums
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Song song = new Song("Test Song", 180L, LocalDate.now(), artist);
        Album album1 = new Album("Album 1", LocalDate.now(), artist);
        Album album2 = new Album("Album 2", LocalDate.now(), artist);

        // Associate song with albums
        album1.addSong(song);
        album2.addSong(song);

        entityManager.persist(song);
        entityManager.persist(album1);
        entityManager.persist(album2);
        flushAndClear();

        // Retrieve and verify many-to-many in both directions
        Song foundSong = entityManager.find(Song.class, song.getId());
        assertThat(foundSong.getAlbums()).hasSize(2);

        Album foundAlbum1 = entityManager.find(Album.class, album1.getId());
        assertThat(foundAlbum1.getSongs()).hasSize(1);
        assertThat(foundAlbum1.getSongs().get(0).getId()).isEqualTo(song.getId());
    }

    @Test
    public void testAlbumToSongsManyToManyRelationship() {
        // Create Album with multiple Songs
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Song song1 = new Song("Song 1", 180L, LocalDate.now(), artist);
        Song song2 = new Song("Song 2", 200L, LocalDate.now(), artist);
        entityManager.persist(song1);
        entityManager.persist(song2);

        Album album = new Album("Test Album", LocalDate.now(), artist);
        album.addSong(song1);
        album.addSong(song2);
        entityManager.persist(album);
        flushAndClear();

        // Retrieve and verify
        Album found = entityManager.find(Album.class, album.getId());
        assertThat(found.getSongs()).hasSize(2);

        // Add another song
        Song song3 = new Song("Song 3", 220L, LocalDate.now(), artist);
        entityManager.persist(song3);
        found.addSong(song3);
        entityManager.persist(found);  // Re-persist to save the relationship
        flushAndClear();

        // Verify join table updated
        Album updated = entityManager.find(Album.class, album.getId());
        assertThat(updated.getSongs()).hasSize(3);
    }

    @Test
    public void testSongRequiresArtist() {
        // Attempt to persist Song without Artist
        Song song = new Song();
        song.setTitle("Test Song");
        song.setLengthInSeconds(180L);

        assertThatThrownBy(() -> {
            entityManager.persist(song);
            flushAndClear();
        }).isInstanceOf(Exception.class);
    }

    @Test
    public void testCascadeDeleteArtistToSongs() {
        // Create Artist with Songs
        Artist artist = new Artist("Test Artist");
        Song song1 = new Song("Song 1", 180L, LocalDate.now(), artist);
        Song song2 = new Song("Song 2", 200L, LocalDate.now(), artist);
        Song song3 = new Song("Song 3", 220L, LocalDate.now(), artist);

        entityManager.persist(artist);
        entityManager.persist(song1);
        entityManager.persist(song2);
        entityManager.persist(song3);
        flushAndClear();

        Long artistId = artist.getId();
        Long song1Id = song1.getId();

        // Delete Artist
        Artist toDelete = entityManager.find(Artist.class, artistId);
        entityManager.remove(toDelete);
        flushAndClear();

        // Verify Songs are deleted
        assertThat(entityManager.find(Song.class, song1Id)).isNull();
        assertThat(songRepository.findAll()).isEmpty();
    }

    @Test
    public void testCascadeDeleteArtistToAlbums() {
        // Create Artist with Albums
        Artist artist = new Artist("Test Artist");
        Album album1 = new Album("Album 1", LocalDate.now(), artist);
        Album album2 = new Album("Album 2", LocalDate.now(), artist);
        Album album3 = new Album("Album 3", LocalDate.now(), artist);

        entityManager.persist(artist);
        entityManager.persist(album1);
        entityManager.persist(album2);
        entityManager.persist(album3);
        flushAndClear();

        Long artistId = artist.getId();

        // Delete Artist
        Artist toDelete = entityManager.find(Artist.class, artistId);
        entityManager.remove(toDelete);
        flushAndClear();

        // Verify Albums are deleted
        assertThat(albumRepository.findAll()).isEmpty();
    }

    @Test
    public void testCascadeDeleteArtistToSongsAndAlbums() {
        // Create complex graph
        Artist artist = new Artist("Test Artist");
        Song song1 = new Song("Song 1", 180L, LocalDate.now(), artist);
        Song song2 = new Song("Song 2", 200L, LocalDate.now(), artist);
        Album album1 = new Album("Album 1", LocalDate.now(), artist);
        Album album2 = new Album("Album 2", LocalDate.now(), artist);

        // Associate songs with albums
        album1.addSong(song1);
        album1.addSong(song2);
        album2.addSong(song1);

        entityManager.persist(artist);
        entityManager.persist(song1);
        entityManager.persist(song2);
        entityManager.persist(album1);
        entityManager.persist(album2);
        flushAndClear();

        Long artistId = artist.getId();

        // Delete Artist
        Artist toDelete = entityManager.find(Artist.class, artistId);
        entityManager.remove(toDelete);
        flushAndClear();

        // Verify all are deleted
        assertThat(songRepository.findAll()).isEmpty();
        assertThat(albumRepository.findAll()).isEmpty();
    }

    @Test
    public void testDeleteSongDoesNotDeleteAlbum() {
        // Create Song on Album
        Artist artist = new Artist("Test Artist");
        Song song = new Song("Test Song", 180L, LocalDate.now(), artist);
        Album album = new Album("Test Album", LocalDate.now(), artist);

        album.addSong(song);

        entityManager.persist(artist);
        entityManager.persist(song);
        entityManager.persist(album);
        flushAndClear();

        Long songId = song.getId();
        Long albumId = album.getId();

        // Delete Song - must remove from albums first
        Song toDelete = entityManager.find(Song.class, songId);
        toDelete.setAlbums(null);
        entityManager.remove(toDelete);
        flushAndClear();

        // Verify Album still exists
        assertThat(entityManager.find(Album.class, albumId)).isNotNull();
    }

    @Test
    public void testDeleteAlbumDoesNotDeleteSongs() {
        // Create Album with Songs
        Artist artist = new Artist("Test Artist");
        Song song1 = new Song("Song 1", 180L, LocalDate.now(), artist);
        Song song2 = new Song("Song 2", 200L, LocalDate.now(), artist);
        Album album = new Album("Test Album", LocalDate.now(), artist);

        album.addSong(song1);
        album.addSong(song2);

        entityManager.persist(artist);
        entityManager.persist(song1);
        entityManager.persist(song2);
        entityManager.persist(album);
        flushAndClear();

        Long albumId = album.getId();
        Long song1Id = song1.getId();

        // Delete Album
        Album toDelete = entityManager.find(Album.class, albumId);
        entityManager.remove(toDelete);
        flushAndClear();

        // Verify Songs still exist
        assertThat(entityManager.find(Song.class, song1Id)).isNotNull();
        assertThat(songRepository.findAll()).hasSize(2);
    }

    @Test
    public void testMultipleSongsOnMultipleAlbums() {
        // Create complex graph: 2 Albums, 3 Songs
        Artist artist = new Artist("Test Artist");
        entityManager.persist(artist);

        Song song1 = new Song("Song 1", 180L, LocalDate.now(), artist);
        Song song2 = new Song("Song 2", 200L, LocalDate.now(), artist);
        Song song3 = new Song("Song 3", 220L, LocalDate.now(), artist);
        entityManager.persist(song1);
        entityManager.persist(song2);
        entityManager.persist(song3);

        Album album1 = new Album("Album 1", LocalDate.now(), artist);
        Album album2 = new Album("Album 2", LocalDate.now(), artist);

        // Song 1 on both albums
        album1.addSong(song1);
        album2.addSong(song1);

        // Song 2 on Album 1 only
        album1.addSong(song2);

        // Song 3 on Album 2 only
        album2.addSong(song3);

        entityManager.persist(album1);
        entityManager.persist(album2);
        flushAndClear();

        // Verify from Album side
        Album foundAlbum1 = entityManager.find(Album.class, album1.getId());
        assertThat(foundAlbum1.getSongs()).hasSize(2);
        assertThat(foundAlbum1.getSongs()).extracting(Song::getTitle)
            .containsExactlyInAnyOrder("Song 1", "Song 2");

        Album foundAlbum2 = entityManager.find(Album.class, album2.getId());
        assertThat(foundAlbum2.getSongs()).hasSize(2);
        assertThat(foundAlbum2.getSongs()).extracting(Song::getTitle)
            .containsExactlyInAnyOrder("Song 1", "Song 3");

        // Verify from Song side
        Song foundSong1 = entityManager.find(Song.class, song1.getId());
        assertThat(foundSong1.getAlbums()).hasSize(2);
    }
}
