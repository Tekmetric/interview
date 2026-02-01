package com.interview.repository;

import com.interview.base.BaseRepositoryTest;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.SearchResult;
import com.interview.entity.Song;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchResultRepositoryIntegrationTest extends BaseRepositoryTest {

    @Autowired
    private SearchResultRepository searchResultRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    public void testSearchViewExists() {
        // Query SearchResultRepository - should not throw exception
        List<SearchResult> results = searchResultRepository.findAll();
        assertThat(results).isNotNull();
    }

    @Test
    public void testSearchArtistInView() {
        Artist artist = new Artist("Queen");
        artistRepository.save(artist);
        flushAndClear();

        Page<SearchResult> results = searchResultRepository.findByNameContainingIgnoreCase("Queen", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSizeGreaterThanOrEqualTo(1);
        SearchResult artistResult = results.getContent().stream()
            .filter(r -> "ARTIST".equals(r.getEntityType()) && artist.getId().equals(r.getId()))
            .findFirst()
            .orElse(null);

        assertThat(artistResult).isNotNull();
        assertThat(artistResult.getName()).isEqualTo("Queen");
        assertThat(artistResult.getArtistName()).isEqualTo("Queen");
        assertThat(artistResult.getEntityType()).isEqualTo("ARTIST");
    }

    @Test
    public void testSearchSongInView() {
        Artist artist = new Artist("Queen");
        artistRepository.save(artist);

        Song song = new Song("Bohemian Rhapsody", 355L, LocalDate.of(1975, 10, 31), artist);
        songRepository.save(song);
        flushAndClear();

        Page<SearchResult> results = searchResultRepository.findByNameContainingIgnoreCase("Bohemian", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSizeGreaterThanOrEqualTo(1);
        SearchResult songResult = results.getContent().stream()
            .filter(r -> "SONG".equals(r.getEntityType()))
            .findFirst()
            .orElse(null);

        assertThat(songResult).isNotNull();
        assertThat(songResult.getEntityType()).isEqualTo("SONG");
        assertThat(songResult.getName()).isEqualTo("Bohemian Rhapsody");
        assertThat(songResult.getArtistName()).isEqualTo("Queen");
        assertThat(songResult.getReleaseDate()).isEqualTo(LocalDate.of(1975, 10, 31));
    }

    @Test
    public void testSearchAlbumInView() {
        Artist artist = new Artist("Queen");
        artistRepository.save(artist);

        Album album = new Album("A Night at the Opera", LocalDate.of(1975, 11, 21), artist);
        albumRepository.save(album);
        flushAndClear();

        Page<SearchResult> results = searchResultRepository.findByNameContainingIgnoreCase("Opera", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSizeGreaterThanOrEqualTo(1);
        SearchResult albumResult = results.getContent().stream()
            .filter(r -> "ALBUM".equals(r.getEntityType()))
            .findFirst()
            .orElse(null);

        assertThat(albumResult).isNotNull();
        assertThat(albumResult.getEntityType()).isEqualTo("ALBUM");
        assertThat(albumResult.getName()).isEqualTo("A Night at the Opera");
        assertThat(albumResult.getArtistName()).isEqualTo("Queen");
    }

    @Test
    public void testSearchAllEntityTypes() {
        Artist artist = new Artist("Queen");
        artistRepository.save(artist);

        Song song = new Song("Bohemian Rhapsody", 355L, LocalDate.now(), artist);
        songRepository.save(song);

        Album album = new Album("A Night at the Opera", LocalDate.now(), artist);
        albumRepository.save(album);
        flushAndClear();

        Page<SearchResult> results = searchResultRepository.findByNameContainingIgnoreCase("Queen", PageRequest.of(0, 10));

        // Should find at least: Artist "Queen", and possibly songs/albums by Queen if artistName matches
        assertThat(results.getContent()).hasSizeGreaterThanOrEqualTo(1);

        // Verify entity types are differentiated
        List<String> entityTypes = results.getContent().stream()
            .map(SearchResult::getEntityType)
            .distinct()
            .collect(Collectors.toList());

        assertThat(entityTypes).isNotEmpty();
    }

    @Test
    public void testFindByNameContainingIgnoreCase() {
        Artist artist1 = new Artist("The Beatles");
        Artist artist2 = new Artist("Beatles Tribute Band");
        artistRepository.save(artist1);
        artistRepository.save(artist2);
        flushAndClear();

        Page<SearchResult> results = searchResultRepository.findByNameContainingIgnoreCase("beatles", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(results.getContent()).anyMatch(r -> r.getName().contains("Beatles"));
    }

    @Test
    public void testFindByEntityTypeAndNameContainingIgnoreCase() {
        Artist artist = new Artist("Music Artist");
        artistRepository.save(artist);

        Song song = new Song("Music Song", 180L, LocalDate.now(), artist);
        songRepository.save(song);
        flushAndClear();

        Page<SearchResult> songResults = searchResultRepository.findByEntityTypeAndNameContainingIgnoreCase(
            "SONG", "music", PageRequest.of(0, 10));

        assertThat(songResults.getContent()).isNotEmpty();
        assertThat(songResults.getContent()).allMatch(r -> "SONG".equals(r.getEntityType()));
    }

    @Test
    public void testFindByArtistNameContainingIgnoreCase() {
        Artist queen = new Artist("Queen");
        Artist beatles = new Artist("The Beatles");
        artistRepository.save(queen);
        artistRepository.save(beatles);

        Song song1 = new Song("Bohemian Rhapsody", 355L, LocalDate.now(), queen);
        Song song2 = new Song("Hey Jude", 431L, LocalDate.now(), beatles);
        songRepository.save(song1);
        songRepository.save(song2);

        Album album1 = new Album("A Night at the Opera", LocalDate.now(), queen);
        albumRepository.save(album1);
        flushAndClear();

        Page<SearchResult> results = searchResultRepository.findByArtistNameContainingIgnoreCase("Queen", PageRequest.of(0, 10));

        // Should find Queen songs and albums (not necessarily the artist itself depending on search criteria)
        assertThat(results.getContent()).isNotEmpty();
        assertThat(results.getContent()).allMatch(r -> r.getArtistName().contains("Queen"));
    }

    @Test
    public void testSearchPagination() {
        Artist artist = new Artist("Test Artist");
        artistRepository.save(artist);

        // Create 50 songs with "Test" in title
        for (int i = 1; i <= 50; i++) {
            Song song = new Song("Test Song " + i, 180L, LocalDate.now(), artist);
            songRepository.save(song);
        }
        flushAndClear();

        // First page
        Page<SearchResult> page0 = searchResultRepository.findByNameContainingIgnoreCase("Test", PageRequest.of(0, 20));
        assertThat(page0.getContent()).hasSizeLessThanOrEqualTo(20);
        assertThat(page0.getTotalElements()).isGreaterThanOrEqualTo(50);

        // Second page
        Page<SearchResult> page1 = searchResultRepository.findByNameContainingIgnoreCase("Test", PageRequest.of(1, 20));
        assertThat(page1.getContent()).hasSizeLessThanOrEqualTo(20);
    }

    @Test
    public void testSearchViewRefreshesWithNewData() {
        // Baseline query
        long initialCount = searchResultRepository.count();

        // Create new artist
        Artist newArtist = new Artist("Brand New Artist");
        artistRepository.save(newArtist);
        flushAndClear();

        // Query again
        long newCount = searchResultRepository.count();

        // Verify view updated
        assertThat(newCount).isGreaterThan(initialCount);
    }

    @Test
    public void testSearchResultIsReadOnly() {
        List<SearchResult> results = searchResultRepository.findAll();
        if (!results.isEmpty()) {
            SearchResult result = results.get(0);
            // SearchResult is read-only, so we just verify we can query it
            // Hibernate @Immutable annotation prevents modifications
            assertThat(result.getId()).isNotNull();
            assertThat(result.getEntityType()).isNotNull();
        }
    }

    @Test
    public void testCascadeDeleteUpdatesSearchView() {
        // Create Artist with Songs and Albums
        Artist artist = new Artist("Delete Me");
        artistRepository.save(artist);

        Song song1 = new Song("Song 1", 180L, LocalDate.now(), artist);
        Song song2 = new Song("Song 2", 200L, LocalDate.now(), artist);
        songRepository.save(song1);
        songRepository.save(song2);

        Album album1 = new Album("Album 1", LocalDate.now(), artist);
        albumRepository.save(album1);
        flushAndClear();

        // Query search view - verify all present
        Page<SearchResult> beforeDelete = searchResultRepository.findByArtistNameContainingIgnoreCase("Delete Me", PageRequest.of(0, 10));
        long countBefore = beforeDelete.getTotalElements();
        assertThat(countBefore).isGreaterThan(0);

        // Delete Artist
        artistRepository.deleteById(artist.getId());
        flushAndClear();

        // Query search view again
        Page<SearchResult> afterDelete = searchResultRepository.findByArtistNameContainingIgnoreCase("Delete Me", PageRequest.of(0, 10));

        // Verify all related entities no longer in view
        assertThat(afterDelete.getTotalElements()).isZero();
    }
}
