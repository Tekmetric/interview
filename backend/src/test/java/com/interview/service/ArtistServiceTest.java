package com.interview.service;

import com.interview.config.ModelMapperConfig;
import com.interview.dto.ArtistDto;
import com.interview.dto.ArtistListDto;
import com.interview.dto.EntityType;
import com.interview.dto.NotificationAction;
import com.interview.entity.Artist;
import com.interview.event.EntityChangeEvent;
import com.interview.repository.ArtistRepository;
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
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ArtistService artistService;

    private Artist testArtist;
    private ArtistDto testArtistDto;
    private ArtistListDto testArtistListDto;

    @Before
    public void setUp() {
        // Use the actual production ModelMapper configuration
        ModelMapperConfig config = new ModelMapperConfig();
        modelMapper = config.modelMapper();

        // Manually create service with real ModelMapper
        artistService = new ArtistService(artistRepository, modelMapper, eventPublisher);

        testArtist = new Artist("The Beatles");
        testArtist.setId(1L);

        testArtistDto = new ArtistDto(1L, "The Beatles");
        testArtistListDto = new ArtistListDto();
        testArtistListDto.setId(1L);
        testArtistListDto.setName("The Beatles");
    }

    @Test
    public void testCreateArtist_Success() {
        // Arrange
        ArtistDto inputDto = new ArtistDto(null, "The Beatles");
        Artist savedArtist = new Artist("The Beatles");
        savedArtist.setId(1L);

        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        // Act
        ArtistDto result = artistService.createArtist(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("The Beatles", result.getName());

        verify(artistRepository).save(any(Artist.class));
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));

        ArgumentCaptor<EntityChangeEvent> eventCaptor = ArgumentCaptor.forClass(EntityChangeEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        EntityChangeEvent event = eventCaptor.getValue();
        assertEquals(NotificationAction.CREATE, event.getAction());
        assertEquals(EntityType.ARTIST, event.getEntityType());
        assertEquals(Long.valueOf(1L), event.getEntityId());
    }

    @Test
    public void testUpdateArtist_Success() {
        // Arrange
        Long artistId = 1L;
        ArtistDto updateDto = new ArtistDto(null, "The Rolling Stones");
        Artist existingArtist = new Artist("The Beatles");
        existingArtist.setId(artistId);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(existingArtist)).thenReturn(existingArtist);

        // Act
        ArtistDto result = artistService.updateArtist(artistId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(artistId, result.getId());
        assertEquals("The Rolling Stones", result.getName());

        verify(artistRepository).findById(artistId);
        verify(artistRepository).save(existingArtist);
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdateArtist_NotFound() {
        // Arrange
        Long artistId = 999L;
        ArtistDto updateDto = new ArtistDto(null, "The Rolling Stones");

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        // Act
        artistService.updateArtist(artistId, updateDto);

        // Assert - exception expected
    }

    @Test
    public void testDeleteArtist_Success() {
        // Arrange
        Long artistId = 1L;

        when(artistRepository.existsById(artistId)).thenReturn(true);

        // Act
        artistService.deleteArtist(artistId);

        // Assert
        verify(artistRepository).existsById(artistId);
        verify(artistRepository).deleteById(artistId);
        verify(eventPublisher).publishEvent(any(EntityChangeEvent.class));

        ArgumentCaptor<EntityChangeEvent> eventCaptor = ArgumentCaptor.forClass(EntityChangeEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        EntityChangeEvent event = eventCaptor.getValue();
        assertEquals(NotificationAction.DELETE, event.getAction());
        assertEquals(EntityType.ARTIST, event.getEntityType());
        assertEquals(artistId, event.getEntityId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteArtist_NotFound() {
        // Arrange
        Long artistId = 999L;

        when(artistRepository.existsById(artistId)).thenReturn(false);

        // Act
        artistService.deleteArtist(artistId);

        // Assert - exception expected
    }

    @Test
    public void testGetArtist_Success() {
        // Arrange
        Long artistId = 1L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(testArtist));

        // Act
        ArtistDto result = artistService.getArtist(artistId);

        // Assert
        assertNotNull(result);
        assertEquals(artistId, result.getId());
        assertEquals("The Beatles", result.getName());

        verify(artistRepository).findById(artistId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetArtist_NotFound() {
        // Arrange
        Long artistId = 999L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        // Act
        artistService.getArtist(artistId);

        // Assert - exception expected
    }

    @Test
    public void testGetAllArtists_Success() {
        // Arrange
        Artist artist1 = new Artist("The Beatles");
        artist1.setId(1L);
        Artist artist2 = new Artist("The Rolling Stones");
        artist2.setId(2L);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Artist> artistPage = new PageImpl<>(Arrays.asList(artist1, artist2), pageable, 2);

        when(artistRepository.findAll(pageable)).thenReturn(artistPage);

        // Act
        Page<ArtistListDto> result = artistService.getAllArtists(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("The Beatles", result.getContent().get(0).getName());
        assertEquals("The Rolling Stones", result.getContent().get(1).getName());
        assertEquals(Integer.valueOf(0), result.getContent().get(0).getAlbumCount());
        assertEquals(Integer.valueOf(0), result.getContent().get(0).getSongCount());

        verify(artistRepository).findAll(pageable);
    }

    @Test
    public void testSearchArtists_Success() {
        // Arrange
        String query = "Beat";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Artist> artistPage = new PageImpl<>(Arrays.asList(testArtist), pageable, 1);

        when(artistRepository.findByNameContainingIgnoreCase(query, pageable)).thenReturn(artistPage);

        // Act
        Page<ArtistListDto> result = artistService.searchArtists(query, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("The Beatles", result.getContent().get(0).getName());

        verify(artistRepository).findByNameContainingIgnoreCase(query, pageable);
    }

    @Test
    public void testSearchArtists_NoResults() {
        // Arrange
        String query = "NonExistent";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Artist> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(artistRepository.findByNameContainingIgnoreCase(query, pageable)).thenReturn(emptyPage);

        // Act
        Page<ArtistListDto> result = artistService.searchArtists(query, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(artistRepository).findByNameContainingIgnoreCase(query, pageable);
    }
}
