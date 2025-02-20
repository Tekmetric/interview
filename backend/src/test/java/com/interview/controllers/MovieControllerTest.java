package com.interview.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.DirectorDTO;
import com.interview.dto.MovieDTO;
import com.interview.models.Actor;
import com.interview.models.Director;
import com.interview.models.Keyword;
import com.interview.models.Movie;
import com.interview.services.MovieService;
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

        when(movieService.saveMovie(any(Movie.class))).thenReturn(movie);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MovieDTO> request = new HttpEntity<>(movieDTO, headers);

        ResponseEntity<MovieDTO> response = restTemplate.postForEntity(baseUrl, request, MovieDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
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
    void testGetMoviesByGenre() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Horror");
        movie.setLanguage("English");

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));

        when(movieService.getMoviesByGenre(anyString(), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?filterType=genre&genre=Horror&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, jsonNode.get("content").size());
        assertEquals("Horror", jsonNode.get("content").get(0).get("genre").asText());
    }

    @Test
    void testGetMoviesByLanguage() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setLanguage("Chinese");

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));

        when(movieService.getMoviesByLanguage(anyString(), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?filterType=language&language=Chinese&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, jsonNode.get("content").size());
        assertEquals("Chinese", jsonNode.get("content").get(0).get("language").asText());
    }

    @Test
    void testGetMoviesByDirector() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setLanguage("English");

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));

        when(movieService.getMoviesByDirector(anyString(), anyString(), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?filterType=director&firstName=Test&lastName=Director&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, jsonNode.get("content").size());
        assertEquals("Test", jsonNode.get("content").get(0).get("director").get("firstName").asText());
        assertEquals("Director", jsonNode.get("content").get(0).get("director").get("lastName").asText());
    }

    @Test
    void testGetMoviesByActor() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setLanguage("English");

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        Actor actor = new Actor();
        actor.setFirstName("Test");
        actor.setLastName("Actor");

        movie.setActors(List.of(actor));

        Page<Movie> page = new PageImpl<>(List.of(movie));

        when(movieService.getMoviesByActor(anyString(), anyString(), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?filterType=actor&firstName=Test&lastName=Actor&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, jsonNode.get("content").size());
        assertEquals("Test", jsonNode.get("content").get(0).get("actors").get(0).get("firstName").asText());
        assertEquals("Actor", jsonNode.get("content").get(0).get("actors").get(0).get("lastName").asText());

    }

    @Test
    void testGetMoviesByKeyword() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setLanguage("English");

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        Keyword keyword = new Keyword();
        keyword.setName("woods");

        movie.setKeywords(List.of(keyword));

        Page<Movie> page = new PageImpl<>(List.of(movie));

        when(movieService.getMoviesByKeyword(anyString(), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?filterType=keyword&keyword=woods&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, jsonNode.get("content").size());

        assertEquals("woods", jsonNode.get("content").get(0).get("keywords").get(0).get("name").asText());
        assertEquals("Test Movie", jsonNode.get("content").get(0).get("title").asText());
    }

    @Test
    void testGetMoviesByReleaseYear() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie 2022");
        movie.setGenre("Action");
        movie.setLanguage("English");
        movie.setReleaseYear(2022);

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));

        when(movieService.getMoviesByReleaseYear(anyInt(), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?filterType=release-year&releaseYear=2022&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, jsonNode.get("content").size());
        assertEquals(2022, jsonNode.get("content").get(0).get("releaseYear").asInt());
        assertEquals("Test Movie 2022", jsonNode.get("content").get(0).get("title").asText());
    }

    @Test
    void testGetMoviesByRating() throws JsonMappingException, JsonProcessingException {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setLanguage("English");
        movie.setRating(new BigDecimal(7.5));

        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Director");

        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));

        when(movieService.getMoviesByMinRating(any(BigDecimal.class), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?filterType=rating&rating=7.5&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, jsonNode.get("content").size());
        assertEquals(7.5, jsonNode.get("content").get(0).get("rating").asDouble());
        assertEquals("Test Movie", jsonNode.get("content").get(0).get("title").asText());
    }

    @Test
    void testFilterWithNoFilterType() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/filter?filterType=&page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
