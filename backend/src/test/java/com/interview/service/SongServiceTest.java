package com.interview.service;

import com.interview.config.ModelMapperConfig;
import com.interview.dto.EntityType;
import com.interview.dto.NotificationAction;
import com.interview.dto.SongDto;
import com.interview.dto.SongListDto;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import com.interview.event.EntityChangeEvent;
import com.interview.repository.AlbumRepository;
import com.interview.repository.ArtistRepository;
import com.interview.repository.SongRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumRepository albumRepository;

    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private SongService songService;

    private Artist testArtist;
    private Song testSong;
    private Album testAlbum;
    private SongDto testSongDto;

    @Before
    public void setUp() {
        // Use the actual production ModelMapper configuration
        ModelMapperConfig config = new ModelMapperConfig();
        modelMapper = config.modelMapper();

        // Manually create service with real ModelMapper
        songService = new SongService(songRepository, artistRepository, albumRepository, modelMapper, eventPublisher);

        testArtist = new Artist("The Beatles");
        testArtist.setId(1L);

        testAlbum = new Album("Abbey Road", LocalDate.of(1969, 9, 26), testArtist);
        testAlbum.setId(1L);

        testSong = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), testArtist);
        testSong.setId(1L);

        testSongDto = new SongDto(1L, "Come Together", 259L, LocalDate.of(1969, 9, 26), 1L, Arrays.asList(1L));
    }

    @Test
    public void testCreateSong_WithoutAlbums() {
        // Arrange
        SongDto inputDto = new SongDto(null, "Hey Jude", 431L, LocalDate.of(1968, 8, 26), 1L, Collections.emptyList());
        Song savedSong = new Song("Hey Jude", 431L, LocalDate.of(1968, 8, 26), testArtist);
        savedSong.setId(2L);

        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(songRepository.save(any(Song.class))).thenReturn(savedSong);

        // Act
        SongDto result = songService.createSong(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(Long.valueOf(2L), result.getId());
        assertEquals("Hey Jude", result.getTitle());
        assertEquals(Long.valueOf(1L), result.getArtistId());

        verify(artistRepository).findById(1L);
        verify(songRepository).save(any(Song.class));
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));
    }

    @Test
    public void testCreateSong_WithAlbums() {
        // Arrange
        SongDto inputDto = new SongDto(null, "Come Together", 259L, LocalDate.of(1969, 9, 26), 1L, Arrays.asList(1L));
        Song savedSong = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), testArtist);
        savedSong.setId(1L);

        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(albumRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(testAlbum));
        when(songRepository.save(any(Song.class))).thenReturn(savedSong);

        // Act
        SongDto result = songService.createSong(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        verify(albumRepository).findAllById(Arrays.asList(1L));
        verify(songRepository).save(any(Song.class));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCreateSong_ArtistNotFound() {
        // Arrange
        SongDto inputDto = new SongDto(null, "Hey Jude", 431L, LocalDate.of(1968, 8, 26), 999L, Collections.emptyList());

        when(artistRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        songService.createSong(inputDto);

        // Assert - exception expected
    }

    @Test
    public void testUpdateSong_Success() {
        // Arrange
        Long songId = 1L;
        SongDto updateDto = new SongDto(null, "Updated Title", 300L, LocalDate.of(1969, 9, 26), 1L, Collections.emptyList());

        when(songRepository.findById(songId)).thenReturn(Optional.of(testSong));
        when(songRepository.save(testSong)).thenReturn(testSong);

        // Act
        SongDto result = songService.updateSong(songId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(Long.valueOf(300L), result.getLengthInSeconds());
        verify(songRepository).findById(songId);
        verify(songRepository).save(testSong);
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdateSong_NotFound() {
        // Arrange
        Long songId = 999L;
        SongDto updateDto = new SongDto(null, "Updated Title", 300L, LocalDate.of(1969, 9, 26), 1L, Collections.emptyList());

        when(songRepository.findById(songId)).thenReturn(Optional.empty());

        // Act
        songService.updateSong(songId, updateDto);

        // Assert - exception expected
    }

    @Test
    public void testDeleteSong_Success() {
        // Arrange
        Long songId = 1L;

        when(songRepository.findById(songId)).thenReturn(Optional.of(testSong));

        // Act
        songService.deleteSong(songId);

        // Assert
        verify(songRepository).findById(songId);
        verify(songRepository).delete(testSong);
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));

        ArgumentCaptor<EntityChangeEvent> eventCaptor = ArgumentCaptor.forClass(EntityChangeEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        EntityChangeEvent event = eventCaptor.getValue();
        assertEquals(NotificationAction.DELETE, event.getAction());
        assertEquals(EntityType.SONG, event.getEntityType());
        assertEquals(songId, event.getEntityId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteSong_NotFound() {
        // Arrange
        Long songId = 999L;

        when(songRepository.findById(songId)).thenReturn(Optional.empty());

        // Act
        songService.deleteSong(songId);

        // Assert - exception expected
    }

    @Test
    public void testGetSong_Success() {
        // Arrange
        Long songId = 1L;

        when(songRepository.findById(songId)).thenReturn(Optional.of(testSong));

        // Act
        SongDto result = songService.getSong(songId);

        // Assert
        assertNotNull(result);
        assertEquals("Come Together", result.getTitle());
        assertEquals(Long.valueOf(259L), result.getLengthInSeconds());
        assertEquals(Long.valueOf(1L), result.getArtistId());
        verify(songRepository).findById(songId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetSong_NotFound() {
        // Arrange
        Long songId = 999L;

        when(songRepository.findById(songId)).thenReturn(Optional.empty());

        // Act
        songService.getSong(songId);

        // Assert - exception expected
    }

    @Test
    public void testGetAllSongs_Success() {
        // Arrange
        Song song1 = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), testArtist);
        song1.setId(1L);
        Song song2 = new Song("Something", 182L, LocalDate.of(1969, 9, 26), testArtist);
        song2.setId(2L);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Song> songPage = new PageImpl<>(Arrays.asList(song1, song2), pageable, 2);

        when(songRepository.findAll(pageable)).thenReturn(songPage);

        // Act
        Page<SongListDto> result = songService.getAllSongs(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Come Together", result.getContent().get(0).getTitle());
        assertEquals("Something", result.getContent().get(1).getTitle());

        verify(songRepository).findAll(pageable);
    }

    @Test
    public void testGetSongsByArtist_Success() {
        // Arrange
        Long artistId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Song> songPage = new PageImpl<>(Arrays.asList(testSong), pageable, 1);

        when(artistRepository.existsById(artistId)).thenReturn(true);
        when(songRepository.findByArtistId(artistId, pageable)).thenReturn(songPage);

        // Act
        Page<SongListDto> result = songService.getSongsByArtist(artistId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Come Together", result.getContent().get(0).getTitle());
        verify(artistRepository).existsById(artistId);
        verify(songRepository).findByArtistId(artistId, pageable);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetSongsByArtist_ArtistNotFound() {
        // Arrange
        Long artistId = 999L;
        Pageable pageable = PageRequest.of(0, 20);

        when(artistRepository.existsById(artistId)).thenReturn(false);

        // Act
        songService.getSongsByArtist(artistId, pageable);

        // Assert - exception expected
    }

    @Test
    public void testGetSongsByAlbum_Success() {
        // Arrange
        Long albumId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Song> songPage = new PageImpl<>(Arrays.asList(testSong), pageable, 1);

        when(albumRepository.existsById(albumId)).thenReturn(true);
        when(songRepository.findByAlbumsId(albumId, pageable)).thenReturn(songPage);

        // Act
        Page<SongListDto> result = songService.getSongsByAlbum(albumId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Come Together", result.getContent().get(0).getTitle());
        verify(albumRepository).existsById(albumId);
        verify(songRepository).findByAlbumsId(albumId, pageable);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetSongsByAlbum_AlbumNotFound() {
        // Arrange
        Long albumId = 999L;
        Pageable pageable = PageRequest.of(0, 20);

        when(albumRepository.existsById(albumId)).thenReturn(false);

        // Act
        songService.getSongsByAlbum(albumId, pageable);

        // Assert - exception expected
    }

    @Test
    public void testSearchSongs_Success() {
        // Arrange
        String query = "Come";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Song> songPage = new PageImpl<>(Arrays.asList(testSong), pageable, 1);

        when(songRepository.findByTitleContainingIgnoreCase(query, pageable)).thenReturn(songPage);

        // Act
        Page<SongListDto> result = songService.searchSongs(query, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Come Together", result.getContent().get(0).getTitle());
        verify(songRepository).findByTitleContainingIgnoreCase(query, pageable);
    }
}
