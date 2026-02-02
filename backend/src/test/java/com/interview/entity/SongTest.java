package com.interview.entity;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for Song entity focusing on bi-directional relationship management.
 */
public class SongTest {

    @Test
    public void testSetArtist_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), null);

        // Act
        song.setArtist(artist);

        // Assert
        assertEquals(artist, song.getArtist());
        assertTrue("Artist should contain the song", artist.getSongs().contains(song));
    }

    @Test
    public void testSetArtist_RemovesFromPreviousArtist() {
        // Arrange
        Artist oldArtist = new Artist("The Beatles");
        Artist newArtist = new Artist("The Rolling Stones");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), oldArtist);
        oldArtist.addSongInternal(song);

        // Act
        song.setArtist(newArtist);

        // Assert
        assertEquals(newArtist, song.getArtist());
        assertFalse("Old artist should not contain the song", oldArtist.getSongs().contains(song));
        assertTrue("New artist should contain the song", newArtist.getSongs().contains(song));
    }

    @Test
    public void testSetAlbums_ReplacesOldWithNew() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);

        Album oldAlbum1 = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Album oldAlbum2 = new Album("Let It Be", LocalDate.of(1970, 5, 8), artist);
        oldAlbum1.addSong(song);
        oldAlbum2.addSong(song);

        Album newAlbum1 = new Album("Greatest Hits", LocalDate.of(1970, 1, 1), artist);
        Album newAlbum2 = new Album("Best Of", LocalDate.of(1971, 1, 1), artist);

        // Act
        song.setAlbums(Arrays.asList(newAlbum1, newAlbum2));

        // Assert
        assertEquals(2, song.getAlbums().size());
        assertTrue("Song should be in new album 1", song.getAlbums().contains(newAlbum1));
        assertTrue("Song should be in new album 2", song.getAlbums().contains(newAlbum2));
        assertFalse("Song should not be in old album 1", song.getAlbums().contains(oldAlbum1));
        assertFalse("Song should not be in old album 2", song.getAlbums().contains(oldAlbum2));

        // Verify bi-directional relationships
        assertTrue("New album 1 should contain song", newAlbum1.getSongs().contains(song));
        assertTrue("New album 2 should contain song", newAlbum2.getSongs().contains(song));
        assertFalse("Old album 1 should not contain song", oldAlbum1.getSongs().contains(song));
        assertFalse("Old album 2 should not contain song", oldAlbum2.getSongs().contains(song));
    }

    @Test
    public void testSetAlbums_WithNull_RemovesFromAll() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);

        Album album1 = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Album album2 = new Album("Let It Be", LocalDate.of(1970, 5, 8), artist);
        album1.addSong(song);
        album2.addSong(song);

        // Act
        song.setAlbums(null);

        // Assert
        assertEquals(0, song.getAlbums().size());
        assertFalse("Album 1 should not contain song", album1.getSongs().contains(song));
        assertFalse("Album 2 should not contain song", album2.getSongs().contains(song));
    }

    @Test
    public void testSetAlbums_WithEmptyList_RemovesFromAll() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);

        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        album.addSong(song);

        // Act
        song.setAlbums(Arrays.asList());

        // Assert
        assertEquals(0, song.getAlbums().size());
        assertFalse("Album should not contain song", album.getSongs().contains(song));
    }

    @Test
    public void testSetAlbums_ManagesBothSides() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);

        Album album1 = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        Album album2 = new Album("Let It Be", LocalDate.of(1970, 5, 8), artist);

        // Act
        song.setAlbums(Arrays.asList(album1, album2));

        // Assert
        assertEquals(2, song.getAlbums().size());
        assertTrue("Song should be in album 1", song.getAlbums().contains(album1));
        assertTrue("Song should be in album 2", song.getAlbums().contains(album2));
        assertTrue("Album 1 should contain song", album1.getSongs().contains(song));
        assertTrue("Album 2 should contain song", album2.getSongs().contains(song));
    }

    @Test
    public void testGetAlbums_ReturnsUnmodifiableList() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);
        Album album = new Album("Abbey Road", LocalDate.of(1969, 9, 26), artist);
        album.addSong(song);

        // Act & Assert
        List<Album> albums = song.getAlbums();
        try {
            albums.add(new Album("Test", LocalDate.now(), artist));
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testGetLength_ConvertsDuration() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), artist);

        // Act
        assertEquals(431L, song.getLength().getSeconds());
    }

    @Test
    public void testSetLength_StoresAsSeconds() {
        // Arrange
        Artist artist = new Artist("The Beatles");
        Song song = new Song("Hey Jude", 0L, LocalDate.of(1968, 8, 26), artist);

        // Act
        song.setLength(java.time.Duration.ofMinutes(7).plusSeconds(11));

        // Assert
        assertEquals(Long.valueOf(431L), song.getLengthInSeconds());
    }

    @Test
    public void testEqualsAndHashCode() {
        Artist redArtist = new Artist("The Beatles");
        Artist blackArtist = new Artist("Pink Floyd");
        Album redAlbum = new Album("The Wall", null, null);
        Album blackAlbum = new Album("Abbey Road", null, null);
        EqualsVerifier.forClass(Song.class).withPrefabValues(Artist.class,
                redArtist, blackArtist).withPrefabValues(Album.class, redAlbum, blackAlbum).usingGetClass()
                .withIgnoredFields("albums", "artist").verify();
    }
}
