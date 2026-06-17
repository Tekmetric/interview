package com.interview.entity;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for Artist entity focusing on bi-directional relationship management.
 */
public class ArtistTest {

    @Test
    public void testAddSong_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), null);

        // Act
        artist.addSong(song);

        // Assert
        assertTrue("Artist should contain song", artist.getSongs().contains(song));
        assertEquals("Song should reference artist", artist, song.getArtist());
    }

    @Test
    public void testAddSong_DoesNotDuplicateIfAlreadyPresent() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);

        // Act
        artist.addSong(song);
        artist.addSong(song); // Add again

        // Assert
        long count = artist.getSongs().stream().filter(s -> s.equals(song)).count();
        assertEquals("Song should only appear once", 1, count);
    }

    @Test
    public void testRemoveSong_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);
        artist.addSongInternal(song);

        // Act
        artist.removeSong(song);

        // Assert
        assertFalse("Artist should not contain song", artist.getSongs().contains(song));
        assertNull("Song should not reference artist", song.getArtist());
    }

    @Test
    public void testSetSongs_ReplacesAllAndManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");

        Song oldSong1 = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);
        Song oldSong2 = new Song("Let It Be", 243L, LocalDate.of(1970, 3, 6), artist);
        artist.addSongInternal(oldSong1);
        artist.addSongInternal(oldSong2);

        Song newSong1 = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), null);
        Song newSong2 = new Song("Something", 182L, LocalDate.of(1969, 9, 26), null);

        // Act
        artist.setSongs(Arrays.asList(newSong1, newSong2));

        // Assert
        assertEquals(2, artist.getSongs().size());
        assertTrue("Artist should contain new song 1", artist.getSongs().contains(newSong1));
        assertTrue("Artist should contain new song 2", artist.getSongs().contains(newSong2));
        assertFalse("Artist should not contain old song 1", artist.getSongs().contains(oldSong1));
        assertFalse("Artist should not contain old song 2", artist.getSongs().contains(oldSong2));

        // Verify bi-directional relationships
        assertEquals("New song 1 should reference artist", artist, newSong1.getArtist());
        assertEquals("New song 2 should reference artist", artist, newSong2.getArtist());
        assertNull("Old song 1 should not reference artist", oldSong1.getArtist());
        assertNull("Old song 2 should not reference artist", oldSong2.getArtist());
    }

    @Test
    public void testAddAlbum_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), null);

        // Act
        artist.addAlbum(album);

        // Assert
        assertTrue("Artist should contain album", artist.getAlbums().contains(album));
        assertEquals("Album should reference artist", artist, album.getArtist());
    }

    @Test
    public void testAddAlbum_DoesNotDuplicateIfAlreadyPresent() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        artist.addAlbum(album);

        // Act
        artist.addAlbum(album); // Add again

        // Assert
        long count = artist.getAlbums().stream().filter(a -> a.equals(album)).count();
        assertEquals("Album should only appear once", 1, count);
    }

    @Test
    public void testRemoveAlbum_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        artist.addAlbum(album);

        // Act
        artist.removeAlbum(album);

        // Assert
        assertFalse("Artist should not contain album", artist.getAlbums().contains(album));
        assertNull("Album should not reference artist", album.getArtist());
    }

    @Test
    public void testSetAlbums_ReplacesAllAndManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");

        Album oldAlbum1 = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Album oldAlbum2 = new Album("Let It Be", LocalDate.of(1970, 5, 8), artist);

        Album newAlbum1 = new Album("Please Please Me", LocalDate.of(1963, 3, 22), null);
        Album newAlbum2 = new Album("With The Beatles", LocalDate.of(1963, 11, 22), null);

        // Act
        artist.setAlbums(Arrays.asList(newAlbum1, newAlbum2));

        // Assert
        assertEquals(2, artist.getAlbums().size());
        assertTrue("Artist should contain new album 1", artist.getAlbums().contains(newAlbum1));
        assertTrue("Artist should contain new album 2", artist.getAlbums().contains(newAlbum2));
        assertFalse("Artist should not contain old album 1", artist.getAlbums().contains(oldAlbum1));
        assertFalse("Artist should not contain old album 2", artist.getAlbums().contains(oldAlbum2));

        // Verify bi-directional relationships
        assertEquals("New album 1 should reference artist", artist, newAlbum1.getArtist());
        assertEquals("New album 2 should reference artist", artist, newAlbum2.getArtist());
        assertNull("Old album 1 should not reference artist", oldAlbum1.getArtist());
        assertNull("Old album 2 should not reference artist", oldAlbum2.getArtist());
    }

    @Test
    public void testGetSongs_ReturnsUnmodifiableList() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);
        artist.addSongInternal(song);

        // Act & Assert
        List<Song> songs = artist.getSongs();
        try {
            songs.add(new Song("Test", 100L, LocalDate.now(), artist));
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testGetAlbums_ReturnsUnmodifiableList() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        artist.addAlbum(album);

        // Act & Assert
        List<Album> albums = artist.getAlbums();
        try {
            albums.add(new Album("Test", LocalDate.now(), artist));
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testMultipleSongsCanBeAddedToArtist() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song1 = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), null);
        Song song2 = new Song("Let It Be", 243L, LocalDate.of(1970, 3, 6), null);
        Song song3 = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), null);

        // Act
        artist.addSong(song1);
        artist.addSong(song2);
        artist.addSong(song3);

        // Assert
        assertEquals(3, artist.getSongs().size());
        assertTrue(artist.getSongs().contains(song1));
        assertTrue(artist.getSongs().contains(song2));
        assertTrue(artist.getSongs().contains(song3));
    }

    @Test
    public void testMultipleAlbumsCanBeAddedToArtist() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album1 = new Album("Abbey Road", LocalDate.of(1969, 9, 26), null);
        Album album2 = new Album("Let It Be", LocalDate.of(1970, 5, 8), null);
        Album album3 = new Album("Sgt. Pepper's", LocalDate.of(1967, 6, 1), null);

        // Act
        artist.addAlbum(album1);
        artist.addAlbum(album2);
        artist.addAlbum(album3);

        // Assert
        assertEquals(3, artist.getAlbums().size());
        assertTrue(artist.getAlbums().contains(album1));
        assertTrue(artist.getAlbums().contains(album2));
        assertTrue(artist.getAlbums().contains(album3));
    }

    @Test
    public void testEqualsAndHashCode() {
        Album redAlbum = new Album("The Wall", null, null);
        Album blackAlbum = new Album("Abbey Road", null, null);
        Song redSong = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), null);
        Song blackSong = new Song("Chutney", 431L, LocalDate.of(1970, 3, 6), null);
        EqualsVerifier.forClass(Artist.class).usingGetClass().withPrefabValues(Song.class, redSong, blackSong)
                .withPrefabValues(Album.class, redAlbum, blackAlbum).withIgnoredFields("songs", "albums");
    }

    @Test
    public void testSetSongs_WithNull_ClearsAllSongs() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song1 = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);
        Song song2 = new Song("Let It Be", 243L, LocalDate.of(1970, 3, 6), artist);
        artist.addSongInternal(song1);
        artist.addSongInternal(song2);

        // Act
        artist.setSongs(null);

        // Assert
        assertEquals(0, artist.getSongs().size());
        assertNull(song1.getArtist());
        assertNull(song2.getArtist());
    }

    @Test
    public void testSetAlbums_WithNull_ClearsAllAlbums() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album1 = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Album album2 = new Album("Let It Be", LocalDate.of(1970, 5, 8), artist);

        // Act
        artist.setAlbums(null);

        // Assert
        assertEquals(0, artist.getAlbums().size());
        assertNull(album1.getArtist());
        assertNull(album2.getArtist());
    }
}
