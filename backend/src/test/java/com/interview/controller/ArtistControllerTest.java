package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.AlbumListDto;
import com.interview.dto.ArtistDto;
import com.interview.dto.ArtistListDto;
import com.interview.dto.SongListDto;
import com.interview.service.AlbumService;
import com.interview.service.ArtistService;
import com.interview.service.SongService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtistControllerTest {

    @Mock
    private ArtistService artistService;

    @Mock
    private SongService songService;

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private ArtistController artistController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ArtistDto testArtistDto;
    private ArtistListDto testArtistListDto;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(artistController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        testArtistDto = new ArtistDto(1L, "The Beatles");

        testArtistListDto = new ArtistListDto();
        testArtistListDto.setId(1L);
        testArtistListDto.setName("The Beatles");
        testArtistListDto.setAlbumCount(2);
        testArtistListDto.setSongCount(5);
    }

    @Test
    public void testGetAllArtists_Success() throws Exception {
        // Arrange
        ArtistListDto artist1 = new ArtistListDto();
        artist1.setId(1L);
        artist1.setName("The Beatles");
        artist1.setAlbumCount(2);
        artist1.setSongCount(5);

        ArtistListDto artist2 = new ArtistListDto();
        artist2.setId(2L);
        artist2.setName("The Rolling Stones");
        artist2.setAlbumCount(1);
        artist2.setSongCount(3);

        Pageable pageable = PageRequest.of(0, 20);
        Page<ArtistListDto> page = new PageImpl<>(Arrays.asList(artist1, artist2), pageable, 2);

        when(artistService.getAllArtists(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("The Beatles")))
                .andExpect(jsonPath("$.content[0].albumCount", is(2)))
                .andExpect(jsonPath("$.content[0].songCount", is(5)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].name", is("The Rolling Stones")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(artistService).getAllArtists(any(Pageable.class));
    }

    @Test
    public void testGetAllArtists_EmptyList() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<ArtistListDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(artistService.getAllArtists(any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(artistService).getAllArtists(any(Pageable.class));
    }

    @Test
    public void testGetArtist_Success() throws Exception {
        // Arrange
        when(artistService.getArtist(1L)).thenReturn(testArtistDto);

        // Act & Assert
        mockMvc.perform(get("/api/artists/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("The Beatles")));

        verify(artistService).getArtist(1L);
    }

    @Test
    public void testGetArtist_NotFound() throws Exception {
        // Arrange
        when(artistService.getArtist(999L)).thenThrow(new EntityNotFoundException("Artist not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/artists/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(artistService).getArtist(999L);
    }

    @Test
    public void testCreateArtist_Success() throws Exception {
        // Arrange
        ArtistDto inputDto = new ArtistDto(null, "Queen");
        ArtistDto createdDto = new ArtistDto(1L, "Queen");

        when(artistService.createArtist(any(ArtistDto.class))).thenReturn(createdDto);

        // Act & Assert
        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Queen")));

        verify(artistService).createArtist(any(ArtistDto.class));
    }

    @Test
    public void testCreateArtist_InvalidInput() throws Exception {
        // Arrange - empty name
        ArtistDto invalidDto = new ArtistDto(null, "");

        // Act & Assert
        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(artistService, never()).createArtist(any(ArtistDto.class));
    }

    @Test
    public void testUpdateArtist_Success() throws Exception {
        // Arrange
        ArtistDto updateDto = new ArtistDto(null, "Updated Name");
        ArtistDto updatedDto = new ArtistDto(1L, "Updated Name");

        when(artistService.updateArtist(eq(1L), any(ArtistDto.class))).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/api/artists/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")));

        verify(artistService).updateArtist(eq(1L), any(ArtistDto.class));
    }

    @Test
    public void testUpdateArtist_NotFound() throws Exception {
        // Arrange
        ArtistDto updateDto = new ArtistDto(null, "Updated Name");

        when(artistService.updateArtist(eq(999L), any(ArtistDto.class)))
                .thenThrow(new EntityNotFoundException("Artist not found with id: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/artists/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(artistService).updateArtist(eq(999L), any(ArtistDto.class));
    }

    @Test
    public void testUpdateArtist_InvalidInput() throws Exception {
        // Arrange - empty name
        ArtistDto invalidDto = new ArtistDto(null, "");

        // Act & Assert
        mockMvc.perform(put("/api/artists/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(artistService, never()).updateArtist(eq(1L), any(ArtistDto.class));
    }

    @Test
    public void testDeleteArtist_Success() throws Exception {
        // Arrange
        doNothing().when(artistService).deleteArtist(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/artists/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(artistService).deleteArtist(1L);
    }

    @Test
    public void testDeleteArtist_NotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Artist not found with id: 999"))
                .when(artistService).deleteArtist(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/artists/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(artistService).deleteArtist(999L);
    }

    @Test
    public void testGetArtistSongs_Success() throws Exception {
        // Arrange
        SongListDto song1 = new SongListDto();
        song1.setId(1L);
        song1.setTitle("Come Together");

        SongListDto song2 = new SongListDto();
        song2.setId(2L);
        song2.setTitle("Something");

        Pageable pageable = PageRequest.of(0, 20);
        Page<SongListDto> page = new PageImpl<>(Arrays.asList(song1, song2), pageable, 2);

        when(songService.getSongsByArtist(eq(1L), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/artists/{id}/songs", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Come Together")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].title", is("Something")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(songService).getSongsByArtist(eq(1L), any(Pageable.class));
    }

    @Test
    public void testGetArtistSongs_ArtistNotFound() throws Exception {
        // Arrange
        when(songService.getSongsByArtist(eq(999L), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("Artist not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/artists/{id}/songs", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(songService).getSongsByArtist(eq(999L), any(Pageable.class));
    }

    @Test
    public void testGetArtistAlbums_Success() throws Exception {
        // Arrange
        AlbumListDto album1 = new AlbumListDto();
        album1.setId(1L);
        album1.setTitle("Abbey Road");

        AlbumListDto album2 = new AlbumListDto();
        album2.setId(2L);
        album2.setTitle("Let It Be");

        Pageable pageable = PageRequest.of(0, 20);
        Page<AlbumListDto> page = new PageImpl<>(Arrays.asList(album1, album2), pageable, 2);

        when(albumService.getAlbumsByArtist(eq(1L), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/artists/{id}/albums", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Abbey Road")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].title", is("Let It Be")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(albumService).getAlbumsByArtist(eq(1L), any(Pageable.class));
    }

    @Test
    public void testGetArtistAlbums_ArtistNotFound() throws Exception {
        // Arrange
        when(albumService.getAlbumsByArtist(eq(999L), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("Artist not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/artists/{id}/albums", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(albumService).getAlbumsByArtist(eq(999L), any(Pageable.class));
    }
}
