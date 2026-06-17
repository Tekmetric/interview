package com.interview.service;

import com.interview.config.ModelMapperConfig;
import com.interview.dto.AlbumDto;
import com.interview.dto.AlbumListDto;
import com.interview.dto.EntityType;
import com.interview.dto.NotificationAction;
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
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private SongRepository songRepository;

    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AlbumService albumService;

    private Artist testArtist;
    private Album testAlbum;
    private Song testSong;
    private AlbumDto testAlbumDto;

    @Before
    public void setUp() {
        // Use the actual production ModelMapper configuration
        ModelMapperConfig config = new ModelMapperConfig();
        modelMapper = config.modelMapper();

        // Manually create service with real ModelMapper
        albumService = new AlbumService(albumRepository, artistRepository, songRepository, modelMapper, eventPublisher);

        testArtist = new Artist("The Beatles");
        testArtist.setId(1L);

        testAlbum = new Album("Abbey Road", LocalDate.of(1969, 9, 26), testArtist);
        testAlbum.setId(1L);

        testSong = new Song("Come Together", 259L, LocalDate.of(1969, 9, 26), testArtist);
        testSong.setId(1L);

        testAlbumDto = new AlbumDto();
        testAlbumDto.setId(1L);
        testAlbumDto.setTitle("Abbey Road");
        testAlbumDto.setReleaseDate(LocalDate.of(1969, 9, 26));
        testAlbumDto.setArtistId(1L);
    }

    @Test
    public void testCreateAlbum_WithoutSongs() {
        // Arrange
        AlbumDto inputDto = new AlbumDto();
        inputDto.setTitle("Abbey Road");
        inputDto.setReleaseDate(LocalDate.of(1969, 9, 26));
        inputDto.setArtistId(1L);
        inputDto.setSongIds(Collections.emptyList());

        Album savedAlbum = new Album("Abbey Road", LocalDate.of(1969, 9, 26), testArtist);
        savedAlbum.setId(1L);

        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(albumRepository.save(any(Album.class))).thenReturn(savedAlbum);

        // Act
        AlbumDto result = albumService.createAlbum(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("Abbey Road", result.getTitle());

        verify(artistRepository).findById(1L);
        verify(albumRepository).save(any(Album.class));
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));
    }

    @Test
    public void testCreateAlbum_WithSongs() {
        // Arrange
        AlbumDto inputDto = new AlbumDto();
        inputDto.setTitle("Abbey Road");
        inputDto.setReleaseDate(LocalDate.of(1969, 9, 26));
        inputDto.setArtistId(1L);
        inputDto.setSongIds(Arrays.asList(1L));

        Album savedAlbum = new Album("Abbey Road", LocalDate.of(1969, 9, 26), testArtist);
        savedAlbum.setId(1L);

        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(songRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(testSong));
        when(albumRepository.save(any(Album.class))).thenReturn(savedAlbum);

        // Act
        AlbumDto result = albumService.createAlbum(inputDto);

        // Assert
        assertNotNull(result);
        verify(songRepository).findAllById(Arrays.asList(1L));
        verify(albumRepository).save(any(Album.class));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCreateAlbum_ArtistNotFound() {
        // Arrange
        AlbumDto inputDto = new AlbumDto();
        inputDto.setTitle("Abbey Road");
        inputDto.setReleaseDate(LocalDate.of(1969, 9, 26));
        inputDto.setArtistId(999L);

        when(artistRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        albumService.createAlbum(inputDto);

        // Assert - exception expected
    }

    @Test
    public void testUpdateAlbum_Success() {
        // Arrange
        Long albumId = 1L;
        AlbumDto updateDto = new AlbumDto();
        updateDto.setTitle("Updated Title");
        updateDto.setReleaseDate(LocalDate.of(1970, 1, 1));
        updateDto.setArtistId(1L);
        updateDto.setSongIds(Collections.emptyList());

        Album updatedAlbum = new Album("Updated Title", LocalDate.of(1970, 1, 1), testArtist);
        updatedAlbum.setId(albumId);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(testAlbum));
        when(albumRepository.save(testAlbum)).thenReturn(updatedAlbum);

        // Act
        AlbumDto result = albumService.updateAlbum(albumId, updateDto);

        // Assert
        assertNotNull(result);
        verify(albumRepository).findById(albumId);
        verify(albumRepository).save(testAlbum);
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdateAlbum_NotFound() {
        // Arrange
        Long albumId = 999L;
        AlbumDto updateDto = new AlbumDto();
        updateDto.setTitle("Updated Title");
        updateDto.setReleaseDate(LocalDate.of(1970, 1, 1));
        updateDto.setArtistId(1L);

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        // Act
        albumService.updateAlbum(albumId, updateDto);

        // Assert - exception expected
    }

    @Test
    public void testUpdateAlbum_ChangeArtist() {
        // Arrange
        Long albumId = 1L;
        Artist newArtist = new Artist("The Rolling Stones");
        newArtist.setId(2L);

        AlbumDto updateDto = new AlbumDto();
        updateDto.setTitle("Abbey Road");
        updateDto.setReleaseDate(LocalDate.of(1969, 9, 26));
        updateDto.setArtistId(2L);
        updateDto.setSongIds(Collections.emptyList());

        Album updatedAlbum = new Album("Abbey Road", LocalDate.of(1969, 9, 26), newArtist);
        updatedAlbum.setId(albumId);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(testAlbum));
        when(artistRepository.findById(2L)).thenReturn(Optional.of(newArtist));
        when(albumRepository.save(testAlbum)).thenReturn(updatedAlbum);

        // Act
        AlbumDto result = albumService.updateAlbum(albumId, updateDto);

        // Assert
        assertNotNull(result);
        verify(artistRepository).findById(2L);
        verify(albumRepository).save(testAlbum);
    }

    @Test
    public void testDeleteAlbum_Success() {
        // Arrange
        Long albumId = 1L;

        when(albumRepository.existsById(albumId)).thenReturn(true);

        // Act
        albumService.deleteAlbum(albumId);

        // Assert
        verify(albumRepository).existsById(albumId);
        verify(albumRepository).deleteById(albumId);
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));

        ArgumentCaptor<EntityChangeEvent> eventCaptor = ArgumentCaptor.forClass(EntityChangeEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        EntityChangeEvent event = eventCaptor.getValue();
        assertEquals(NotificationAction.DELETE, event.getAction());
        assertEquals(EntityType.ALBUM, event.getEntityType());
        assertEquals(albumId, event.getEntityId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteAlbum_NotFound() {
        // Arrange
        Long albumId = 999L;

        when(albumRepository.existsById(albumId)).thenReturn(false);

        // Act
        albumService.deleteAlbum(albumId);

        // Assert - exception expected
    }

    @Test
    public void testGetAlbum_Success() {
        // Arrange
        Long albumId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(testAlbum));

        // Act
        AlbumDto result = albumService.getAlbum(albumId);

        // Assert
        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("Abbey Road", result.getTitle());

        verify(albumRepository).findById(albumId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetAlbum_NotFound() {
        // Arrange
        Long albumId = 999L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        // Act
        albumService.getAlbum(albumId);

        // Assert - exception expected
    }

    @Test
    public void testGetAllAlbums_Success() {
        // Arrange
        Album album1 = new Album("Abbey Road", LocalDate.of(1969, 9, 26), testArtist);
        album1.setId(1L);
        Album album2 = new Album("Let It Be", LocalDate.of(1970, 5, 8), testArtist);
        album2.setId(2L);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Album> albumPage = new PageImpl<>(Arrays.asList(album1, album2), pageable, 2);

        when(albumRepository.findAll(pageable)).thenReturn(albumPage);

        // Act
        Page<AlbumListDto> result = albumService.getAllAlbums(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Abbey Road", result.getContent().get(0).getTitle());
        assertEquals("Let It Be", result.getContent().get(1).getTitle());

        verify(albumRepository).findAll(pageable);
    }

    @Test
    public void testGetAlbumsByArtist_Success() {
        // Arrange
        Long artistId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Album> albumPage = new PageImpl<>(Arrays.asList(testAlbum), pageable, 1);

        when(artistRepository.existsById(artistId)).thenReturn(true);
        when(albumRepository.findByArtistId(artistId, pageable)).thenReturn(albumPage);

        // Act
        Page<AlbumListDto> result = albumService.getAlbumsByArtist(artistId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Abbey Road", result.getContent().get(0).getTitle());
        verify(artistRepository).existsById(artistId);
        verify(albumRepository).findByArtistId(artistId, pageable);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetAlbumsByArtist_ArtistNotFound() {
        // Arrange
        Long artistId = 999L;
        Pageable pageable = PageRequest.of(0, 20);

        when(artistRepository.existsById(artistId)).thenReturn(false);

        // Act
        albumService.getAlbumsByArtist(artistId, pageable);

        // Assert - exception expected
    }

    @Test
    public void testSearchAlbums_Success() {
        // Arrange
        String query = "Abbey";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Album> albumPage = new PageImpl<>(Arrays.asList(testAlbum), pageable, 1);

        when(albumRepository.findByTitleContainingIgnoreCase(query, pageable)).thenReturn(albumPage);

        // Act
        Page<AlbumListDto> result = albumService.searchAlbums(query, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Abbey Road", result.getContent().get(0).getTitle());
        verify(albumRepository).findByTitleContainingIgnoreCase(query, pageable);
    }

    @Test
    public void testSearchAlbums_NoResults() {
        // Arrange
        String query = "NonExistent";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Album> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(albumRepository.findByTitleContainingIgnoreCase(query, pageable)).thenReturn(emptyPage);

        // Act
        Page<AlbumListDto> result = albumService.searchAlbums(query, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(albumRepository).findByTitleContainingIgnoreCase(query, pageable);
    }
}
