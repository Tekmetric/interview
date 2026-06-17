package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.SongDto;
import com.interview.dto.SongListDto;
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
public class SongControllerTest {

    @Mock
    private SongService songService;

    @InjectMocks
    private SongController songController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private SongDto testSongDto;
    private SongListDto testSongListDto;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register JavaTimeModule for LocalDate

        mockMvc = MockMvcBuilders.standaloneSetup(songController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        testSongDto = new SongDto(1L, "Come Together", 259L, LocalDate.of(1969, 9, 26), 1L, Arrays.asList(1L));

        testSongListDto = new SongListDto();
        testSongListDto.setId(1L);
        testSongListDto.setTitle("Come Together");
        testSongListDto.setLengthInSeconds(259L);
        testSongListDto.setReleaseDate(LocalDate.of(1969, 9, 26));
    }

    @Test
    public void testGetAllSongs_Success() throws Exception {
        // Arrange
        SongListDto song1 = new SongListDto();
        song1.setId(1L);
        song1.setTitle("Come Together");
        song1.setLengthInSeconds(259L);

        SongListDto song2 = new SongListDto();
        song2.setId(2L);
        song2.setTitle("Something");
        song2.setLengthInSeconds(182L);

        Pageable pageable = PageRequest.of(0, 20);
        Page<SongListDto> page = new PageImpl<>(Arrays.asList(song1, song2), pageable, 2);

        when(songService.getAllSongs(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Come Together")))
                .andExpect(jsonPath("$.content[0].lengthInSeconds", is(259)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].title", is("Something")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(songService).getAllSongs(any(Pageable.class));
    }

    @Test
    public void testGetAllSongs_EmptyList() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<SongListDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(songService.getAllSongs(any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(songService).getAllSongs(any(Pageable.class));
    }

    @Test
    public void testGetSong_Success() throws Exception {
        // Arrange
        when(songService.getSong(1L)).thenReturn(testSongDto);

        // Act & Assert
        mockMvc.perform(get("/api/songs/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Come Together")))
                .andExpect(jsonPath("$.lengthInSeconds", is(259)));

        verify(songService).getSong(1L);
    }

    @Test
    public void testGetSong_NotFound() throws Exception {
        // Arrange
        when(songService.getSong(999L)).thenThrow(new EntityNotFoundException("Song not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/songs/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(songService).getSong(999L);
    }

    @Test
    public void testCreateSong_Success() throws Exception {
        // Arrange
        SongDto inputDto = new SongDto(null, "Hey Jude", 431L, LocalDate.of(1968, 8, 26), 1L, Collections.emptyList());
        SongDto createdDto = new SongDto(2L, "Hey Jude", 431L, LocalDate.of(1968, 8, 26), 1L, Collections.emptyList());

        when(songService.createSong(any(SongDto.class))).thenReturn(createdDto);

        // Act & Assert
        mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("Hey Jude")))
                .andExpect(jsonPath("$.lengthInSeconds", is(431)));

        verify(songService).createSong(any(SongDto.class));
    }

    @Test
    public void testUpdateSong_Success() throws Exception {
        // Arrange
        SongDto updateDto = new SongDto(null, "Updated Title", 300L, LocalDate.of(1969, 9, 26), 1L, Collections.emptyList());
        SongDto updatedDto = new SongDto(1L, "Updated Title", 300L, LocalDate.of(1969, 9, 26), 1L, Collections.emptyList());

        when(songService.updateSong(eq(1L), any(SongDto.class))).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/api/songs/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.lengthInSeconds", is(300)));

        verify(songService).updateSong(eq(1L), any(SongDto.class));
    }

    @Test
    public void testUpdateSong_NotFound() throws Exception {
        // Arrange
        SongDto updateDto = new SongDto(null, "Updated Title", 300L, LocalDate.of(1969, 9, 26), 1L, Collections.emptyList());

        when(songService.updateSong(eq(999L), any(SongDto.class)))
                .thenThrow(new EntityNotFoundException("Song not found with id: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/songs/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(songService).updateSong(eq(999L), any(SongDto.class));
    }

    @Test
    public void testDeleteSong_Success() throws Exception {
        // Arrange
        doNothing().when(songService).deleteSong(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/songs/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(songService).deleteSong(1L);
    }

    @Test
    public void testDeleteSong_NotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Song not found with id: 999"))
                .when(songService).deleteSong(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/songs/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(songService).deleteSong(999L);
    }
}
