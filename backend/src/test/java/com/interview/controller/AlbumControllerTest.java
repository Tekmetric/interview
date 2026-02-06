package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.AlbumDto;
import com.interview.dto.AlbumListDto;
import com.interview.dto.SongListDto;
import com.interview.service.AlbumService;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AlbumControllerTest {

    @Mock
    private AlbumService albumService;

    @Mock
    private SongService songService;

    @InjectMocks
    private AlbumController albumController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private AlbumDto testAlbumDto;
    private AlbumListDto testAlbumListDto;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register JavaTimeModule for LocalDate

        mockMvc = MockMvcBuilders.standaloneSetup(albumController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        testAlbumDto = new AlbumDto();
        testAlbumDto.setId(1L);
        testAlbumDto.setTitle("Abbey Road");
        testAlbumDto.setReleaseDate(LocalDate.of(1969, 9, 26));
        testAlbumDto.setArtistId(1L);
        testAlbumDto.setSongIds(Arrays.asList(1L, 2L));

        testAlbumListDto = new AlbumListDto();
        testAlbumListDto.setId(1L);
        testAlbumListDto.setTitle("Abbey Road");
        testAlbumListDto.setReleaseDate(LocalDate.of(1969, 9, 26));
    }

    @Test
    public void testGetAllAlbums_Success() throws Exception {
        // Arrange
        AlbumListDto album1 = new AlbumListDto();
        album1.setId(1L);
        album1.setTitle("Abbey Road");

        AlbumListDto album2 = new AlbumListDto();
        album2.setId(2L);
        album2.setTitle("Let It Be");

        Pageable pageable = PageRequest.of(0, 20);
        Page<AlbumListDto> page = new PageImpl<>(Arrays.asList(album1, album2), pageable, 2);

        when(albumService.getAllAlbums(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Abbey Road")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].title", is("Let It Be")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(albumService).getAllAlbums(any(Pageable.class));
    }

    @Test
    public void testGetAllAlbums_EmptyList() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<AlbumListDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(albumService.getAllAlbums(any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(albumService).getAllAlbums(any(Pageable.class));
    }

    @Test
    public void testGetAlbum_Success() throws Exception {
        // Arrange
        when(albumService.getAlbum(1L)).thenReturn(testAlbumDto);

        // Act & Assert
        mockMvc.perform(get("/api/albums/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Abbey Road")));

        verify(albumService).getAlbum(1L);
    }

    @Test
    public void testGetAlbum_NotFound() throws Exception {
        // Arrange
        when(albumService.getAlbum(999L)).thenThrow(new EntityNotFoundException("Album not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/albums/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(albumService).getAlbum(999L);
    }

    @Test
    public void testCreateAlbum_Success() throws Exception {
        // Arrange
        AlbumDto inputDto = new AlbumDto();
        inputDto.setTitle("Rubber Soul");
        inputDto.setReleaseDate(LocalDate.of(1965, 12, 3));
        inputDto.setArtistId(1L);
        inputDto.setSongIds(Collections.emptyList());

        AlbumDto createdDto = new AlbumDto();
        createdDto.setId(2L);
        createdDto.setTitle("Rubber Soul");
        createdDto.setReleaseDate(LocalDate.of(1965, 12, 3));
        createdDto.setArtistId(1L);

        when(albumService.createAlbum(any(AlbumDto.class))).thenReturn(createdDto);

        // Act & Assert
        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("Rubber Soul")));

        verify(albumService).createAlbum(any(AlbumDto.class));
    }

    @Test
    public void testUpdateAlbum_Success() throws Exception {
        // Arrange
        AlbumDto updateDto = new AlbumDto();
        updateDto.setTitle("Updated Title");
        updateDto.setReleaseDate(LocalDate.of(1970, 1, 1));
        updateDto.setArtistId(1L);
        updateDto.setSongIds(Collections.emptyList());

        AlbumDto updatedDto = new AlbumDto();
        updatedDto.setId(1L);
        updatedDto.setTitle("Updated Title");
        updatedDto.setReleaseDate(LocalDate.of(1970, 1, 1));
        updatedDto.setArtistId(1L);

        when(albumService.updateAlbum(eq(1L), any(AlbumDto.class))).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/api/albums/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Title")));

        verify(albumService).updateAlbum(eq(1L), any(AlbumDto.class));
    }

    @Test
    public void testUpdateAlbum_NotFound() throws Exception {
        // Arrange
        AlbumDto updateDto = new AlbumDto();
        updateDto.setTitle("Updated Title");
        updateDto.setReleaseDate(LocalDate.of(1970, 1, 1));
        updateDto.setArtistId(1L);

        when(albumService.updateAlbum(eq(999L), any(AlbumDto.class)))
                .thenThrow(new EntityNotFoundException("Album not found with id: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/albums/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(albumService).updateAlbum(eq(999L), any(AlbumDto.class));
    }

    @Test
    public void testDeleteAlbum_Success() throws Exception {
        // Arrange
        doNothing().when(albumService).deleteAlbum(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/albums/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(albumService).deleteAlbum(1L);
    }

    @Test
    public void testDeleteAlbum_NotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Album not found with id: 999"))
                .when(albumService).deleteAlbum(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/albums/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(albumService).deleteAlbum(999L);
    }

    @Test
    public void testGetAlbumSongs_Success() throws Exception {
        // Arrange
        SongListDto song1 = new SongListDto();
        song1.setId(1L);
        song1.setTitle("Come Together");

        SongListDto song2 = new SongListDto();
        song2.setId(2L);
        song2.setTitle("Something");

        Pageable pageable = PageRequest.of(0, 20);
        Page<SongListDto> page = new PageImpl<>(Arrays.asList(song1, song2), pageable, 2);

        when(songService.getSongsByAlbum(eq(1L), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/albums/{id}/songs", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Come Together")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].title", is("Something")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(songService).getSongsByAlbum(eq(1L), any(Pageable.class));
    }

    @Test
    public void testGetAlbumSongs_AlbumNotFound() throws Exception {
        // Arrange
        when(songService.getSongsByAlbum(eq(999L), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("Album not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/albums/{id}/songs", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(songService).getSongsByAlbum(eq(999L), any(Pageable.class));
    }
}
