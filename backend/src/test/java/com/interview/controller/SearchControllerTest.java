package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.SearchResultDto;
import com.interview.service.SearchService;
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

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    public void testSearch_GeneralQuery() throws Exception {
        // Arrange
        SearchResultDto result1 = new SearchResultDto();
        result1.setEntityType("ARTIST");
        result1.setId(1L);
        result1.setName("Queen");

        SearchResultDto result2 = new SearchResultDto();
        result2.setEntityType("SONG");
        result2.setId(2L);
        result2.setName("Bohemian Rhapsody");

        Pageable pageable = PageRequest.of(0, 20);
        Page<SearchResultDto> page = new PageImpl<>(Arrays.asList(result1, result2), pageable, 2);

        when(searchService.search(eq("Queen"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/search")
                        .param("q", "Queen")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].entityType", is("ARTIST")))
                .andExpect(jsonPath("$.content[0].name", is("Queen")))
                .andExpect(jsonPath("$.content[1].entityType", is("SONG")))
                .andExpect(jsonPath("$.content[1].name", is("Bohemian Rhapsody")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(searchService).search(eq("Queen"), any(Pageable.class));
    }

    @Test
    public void testSearch_ByType() throws Exception {
        // Arrange
        SearchResultDto result = new SearchResultDto();
        result.setEntityType("SONG");
        result.setId(1L);
        result.setName("Come Together");

        Pageable pageable = PageRequest.of(0, 20);
        Page<SearchResultDto> page = new PageImpl<>(Collections.singletonList(result), pageable, 1);

        when(searchService.searchByType(eq("Come"), eq("SONG"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/search")
                        .param("q", "Come")
                        .param("type", "SONG")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].entityType", is("SONG")))
                .andExpect(jsonPath("$.content[0].name", is("Come Together")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(searchService).searchByType(eq("Come"), eq("SONG"), any(Pageable.class));
    }

    @Test
    public void testSearch_ByArtist() throws Exception {
        // Arrange
        SearchResultDto result1 = new SearchResultDto();
        result1.setEntityType("SONG");
        result1.setId(1L);
        result1.setName("Come Together");
        result1.setArtistName("The Beatles");

        SearchResultDto result2 = new SearchResultDto();
        result2.setEntityType("ALBUM");
        result2.setId(2L);
        result2.setName("Abbey Road");
        result2.setArtistName("The Beatles");

        Pageable pageable = PageRequest.of(0, 20);
        Page<SearchResultDto> page = new PageImpl<>(Arrays.asList(result1, result2), pageable, 2);

        when(searchService.searchByArtist(eq("The Beatles"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/search")
                        .param("artist", "The Beatles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].entityType", is("SONG")))
                .andExpect(jsonPath("$.content[0].artistName", is("The Beatles")))
                .andExpect(jsonPath("$.content[1].entityType", is("ALBUM")))
                .andExpect(jsonPath("$.content[1].artistName", is("The Beatles")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(searchService).searchByArtist(eq("The Beatles"), any(Pageable.class));
    }

    @Test
    public void testSearch_NoParameters() throws Exception {
        // Act & Assert - should return empty page when no parameters provided
        mockMvc.perform(get("/api/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        // No service methods should be called
        verifyNoInteractions(searchService);
    }

    @Test
    public void testSearch_EmptyQueryParameter() throws Exception {
        // Act & Assert - should return empty page when query is empty
        mockMvc.perform(get("/api/search")
                        .param("q", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verifyNoInteractions(searchService);
    }

    @Test
    public void testSearch_EmptyArtistParameter() throws Exception {
        // Act & Assert - should return empty page when artist is empty
        mockMvc.perform(get("/api/search")
                        .param("artist", "   ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verifyNoInteractions(searchService);
    }

    @Test
    public void testSearch_NoResults() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<SearchResultDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(searchService.search(eq("NonExistent"), any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/search")
                        .param("q", "NonExistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(searchService).search(eq("NonExistent"), any(Pageable.class));
    }

    @Test
    public void testSearch_TypeParameterCaseInsensitive() throws Exception {
        // Arrange
        SearchResultDto result = new SearchResultDto();
        result.setEntityType("ALBUM");
        result.setId(1L);
        result.setName("Abbey Road");

        Pageable pageable = PageRequest.of(0, 20);
        Page<SearchResultDto> page = new PageImpl<>(Collections.singletonList(result), pageable, 1);

        when(searchService.searchByType(eq("Abbey"), eq("ALBUM"), any(Pageable.class))).thenReturn(page);

        // Act & Assert - lowercase type parameter should be converted to uppercase
        mockMvc.perform(get("/api/search")
                        .param("q", "Abbey")
                        .param("type", "album")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].entityType", is("ALBUM")));

        verify(searchService).searchByType(eq("Abbey"), eq("ALBUM"), any(Pageable.class));
    }
}
