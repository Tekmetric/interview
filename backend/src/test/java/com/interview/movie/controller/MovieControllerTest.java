package com.interview.movie.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.actor.model.Actor;
import com.interview.director.dto.DirectorDTO;
import com.interview.director.model.Director;
import com.interview.keyword.model.Keyword;
import com.interview.movie.dto.MovieDTO;
import com.interview.movie.model.Movie;
import com.interview.movie.service.MovieService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @SuppressWarnings("removal")
    @MockBean
    private MovieService movieService;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/movie";
    }

    @Test
    void testGetMoviesPaged() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setLanguage("English");

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));
        PageRequest pageable = PageRequest.of(0, 10);

        when(movieService.getMovies(pageable)).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "?page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, jsonNode.get("content").size());
        assertEquals("Test Movie", jsonNode.get("content").get(0).get("title").asText());
    }

    @Test
    void testGetMovieById() {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setLanguage("English");

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        when(movieService.getMovieById(1L)).thenReturn(movie);

        ResponseEntity<MovieDTO> response = restTemplate.getForEntity(baseUrl + "/1", MovieDTO.class);

        MovieDTO dto = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(dto);
        assertEquals("Test Movie", dto.getTitle());
        assertEquals("Action", dto.getGenre());
        assertEquals("English", dto.getLanguage());
        assertEquals("Test", dto.getDirector().getFirstName());
        assertEquals("Director", dto.getDirector().getLastName());

    }

    @Test
    void testSaveMovie() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Test Movie");
        movieDTO.setGenre("Action");
        movieDTO.setLanguage("English");
        movieDTO.setDuration(120);

        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setFirstName("Test");
        directorDTO.setLastName("Director");

        movieDTO.setDirector(directorDTO);

        Movie movie = new Movie();
        movie.setTitle("New Movie");
        movie.setGenre("Action");
        movie.setLanguage("English");

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        when(movieService.createMovie(any(Movie.class))).thenReturn(movie);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MovieDTO> request = new HttpEntity<>(movieDTO, headers);

        ResponseEntity<MovieDTO> response = restTemplate.postForEntity(baseUrl, request, MovieDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteMovie() {
        doNothing().when(movieService).deleteMovieById(anyLong());

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).deleteMovieById(1L);
    }

    @Test
    void testUpdateMovie() {
        try {
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setTitle("Test Movie");
            movieDTO.setGenre("Action");
            movieDTO.setLanguage("English");
            movieDTO.setDuration(120);

            DirectorDTO directorDTO = new DirectorDTO();
            directorDTO.setId(1L);

            movieDTO.setDirector(directorDTO);

            Movie returned = new Movie(movieDTO);
            returned.setTitle("Updated Movie");

            when(movieService.updateMovie(anyLong(), any(Movie.class))).thenReturn(returned);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<MovieDTO> request = new HttpEntity<>(movieDTO, headers);

            ResponseEntity<MovieDTO> response = restTemplate.exchange(
                    baseUrl + "/1",
                    HttpMethod.PUT,
                    request,
                    MovieDTO.class);

            MovieDTO dto = response.getBody();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(dto);
            assertEquals("Updated Movie", dto.getTitle());
            assertEquals("Action", dto.getGenre());
            assertEquals("English", dto.getLanguage());
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Test
    void testGetMoviesFilter() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setLanguage("English");
        movie.setRating(new BigDecimal(8));
        movie.setReleaseYear(2010);

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        Actor actor = new Actor();
        actor.setFirstName("Test");
        actor.setLastName("Actor");

        Keyword keyword = new Keyword();
        keyword.setName("Action");

        movie.setKeywords(List.of(keyword));
        movie.setActors(List.of(actor));
        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));
        PageRequest pageable = PageRequest.of(0, 10);

        when(movieService.getMoviesByFilter(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyInt(), any(), eq(pageable))).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?genre=Action&actorFirstName=Test&actorLastName=Actor&keyword=Action&language=English&directorFirstName=Test&directorLastName=Director&releaseYear=2010&rating=8&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(1, jsonNode.get("content").size());

        JsonNode movieNode = jsonNode.get("content").get(0);
        assertEquals(movie.getTitle(), movieNode.get("title").asText());
        assertEquals(movie.getGenre(), movieNode.get("genre").asText());
        assertEquals(movie.getLanguage(), movieNode.get("language").asText());
        assertEquals(movie.getRating().intValue(), movieNode.get("rating").asInt());
        assertEquals(movie.getReleaseYear(), movieNode.get("releaseYear").asInt());

        JsonNode directorNode = movieNode.get("director");
        assertNotNull(directorNode);
        assertEquals(director.getFirstName(), directorNode.get("firstName").asText());
        assertEquals(director.getLastName(), directorNode.get("lastName").asText());

        JsonNode actorNode = movieNode.get("actors").get(0);
        assertNotNull(actorNode);
        assertEquals(actor.getFirstName(), actorNode.get("firstName").asText());
        assertEquals(actor.getLastName(), actorNode.get("lastName").asText());

        JsonNode keywordNode = movieNode.get("keywords").get(0);
        assertNotNull(keywordNode);
        assertEquals(keyword.getName(), keywordNode.get("name").asText());
    }
}
