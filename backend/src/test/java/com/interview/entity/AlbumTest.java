package com.interview.entity;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for Album entity focusing on bi-directional relationship management.
 */
public class AlbumTest {

    @Test
    public void testSetArtist_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), null);

        // Act
        album.setArtist(artist);

        // Assert
        assertEquals(artist, album.getArtist());
        assertTrue("Artist should contain the album", artist.getAlbums().contains(album));
    }

    @Test
    public void testSetArtist_RemovesFromPreviousArtist() {
        // Arrange
        Artist oldArtist = new Artist("The Beatles");
        Artist newArtist = new Artist("The Rolling Stones");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), oldArtist);

        // Act
        album.setArtist(newArtist);

        // Assert
        assertEquals(newArtist, album.getArtist());
        assertFalse("Old artist should not contain album", oldArtist.getAlbums().contains(album));
        assertTrue("New artist should contain album", newArtist.getAlbums().contains(album));
    }

    @Test
    public void testAddSong_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Song song = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist);

        // Act
        album.addSong(song);

        // Assert
        assertTrue("Album should contain song", album.getSongs().contains(song));
        assertTrue("Song should be in album", song.getAlbums().contains(album));
    }

    @Test
    public void testAddSong_DoesNotDuplicateIfAlreadyPresent() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Song song = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist);

        // Act
        album.addSong(song);
        album.addSong(song); // Add again

        // Assert
        long count = album.getSongs().stream().filter(s -> s.equals(song)).count();
        assertEquals("Song should only appear once", 1, count);
    }

    @Test
    public void testRemoveSong_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Song song = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist);
        album.addSong(song);

        // Act
        album.removeSong(song);

        // Assert
        assertFalse("Album should not contain song", album.getSongs().contains(song));
        assertFalse("Song should not be in album", song.getAlbums().contains(album));
    }

    @Test
    public void testSetSongs_ReplacesAllAndManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);

        Song oldSong1 = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist);
        Song oldSong2 = new Song("Something", 182L, LocalDate.of(1969, 9, 26), artist);
        album.addSong(oldSong1);
        album.addSong(oldSong2);

        Song newSong1 = new Song("Here Comes The Sun", 185L, LocalDate.of(1969, 9, 26), artist);
        Song newSong2 = new Song("Octopus's Garden", 171L, LocalDate.of(1969, 9, 26), artist);

        // Act
        album.setSongs(Arrays.asList(newSong1, newSong2));

        // Assert
        assertEquals(2, album.getSongs().size());
        assertTrue("Album should contain new song 1", album.getSongs().contains(newSong1));
        assertTrue("Album should contain new song 2", album.getSongs().contains(newSong2));
        assertFalse("Album should not contain old song 1", album.getSongs().contains(oldSong1));
        assertFalse("Album should not contain old song 2", album.getSongs().contains(oldSong2));

        // Verify bi-directional relationships
        assertTrue("New song 1 should be in album", newSong1.getAlbums().contains(album));
        assertTrue("New song 2 should be in album", newSong2.getAlbums().contains(album));
        assertFalse("Old song 1 should not be in album", oldSong1.getAlbums().contains(album));
        assertFalse("Old song 2 should not be in album", oldSong2.getAlbums().contains(album));
    }

    @Test
    public void testSetSongs_WithEmptyList_RemovesAll() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);

        Song song1 = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist);
        Song song2 = new Song("Something", 182L, LocalDate.of(1969, 9, 26), artist);
        album.addSong(song1);
        album.addSong(song2);

        // Act
        album.setSongs(Arrays.asList());

        // Assert
        assertEquals(0, album.getSongs().size());
        assertFalse("Song 1 should not be in album", song1.getAlbums().contains(album));
        assertFalse("Song 2 should not be in album", song2.getAlbums().contains(album));
    }

    @Test
    public void testGetSongs_ReturnsUnmodifiableList() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Song song = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist);
        album.addSong(song);

        // Act & Assert
        List<Song> songs = album.getSongs();
        try {
            songs.add(new Song("Test", 100L, LocalDate.now(), artist));
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testMultipleSongsCanBeAddedToAlbum() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);

        Song song1 = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist);
        Song song2 = new Song("Something", 182L, LocalDate.of(1969, 9, 26), artist);
        Song song3 = new Song("Here Comes The Sun", 185L, LocalDate.of(1969, 9, 26), artist);

        // Act
        album.addSong(song1);
        album.addSong(song2);
        album.addSong(song3);

        // Assert
        assertEquals(3, album.getSongs().size());
        assertTrue(album.getSongs().contains(song1));
        assertTrue(album.getSongs().contains(song2));
        assertTrue(album.getSongs().contains(song3));
    }

    @Test
    public void testRemoveSong_WhenSongNotInAlbum_DoesNotFail() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Song song = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), artist);

        // Act - should not throw exception
        album.removeSong(song);

        // Assert
        assertEquals(0, album.getSongs().size());
    }

    @Test
    public void testEqualsAndHashCode() {
        Artist redArtist = new Artist("The Beatles");
        Artist blackArtist = new Artist("Pink Floyd");
        Song redSong = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), null);
        Song blackSong = new Song("Chutney", 431L, LocalDate.of(1970, 3, 6), null);
        EqualsVerifier.forClass(Album.class).usingGetClass().withIgnoredFields("artist", "songs")
                .withPrefabValues(Artist.class, redArtist, blackArtist)
                .withPrefabValues(Song.class, redSong, blackSong).verify();
    }
}
