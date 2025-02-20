package com.interview.movie.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.interview.director.dto.DirectorDTO;
import com.interview.movie.dto.MovieDTO;

public class MovieTest {
    @Test
    void testMovieConstructorFromDTO() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Inception");
        movieDTO.setDescription("A mind-bending thriller");
        movieDTO.setGenre("Sci-Fi");
        movieDTO.setDirector(new DirectorDTO() {
            {
                setFirstName("Christopher");
                setLastName("Nolan");
            }
        });
        movieDTO.setReleaseYear(2010);
        movieDTO.setDuration(148);
        movieDTO.setLanguage("English");
        movieDTO.setBudget(new BigDecimal("160000000"));
        movieDTO.setBoxOffice(new BigDecimal("830000000"));

        Movie movie = new Movie(movieDTO);

        assertNotNull(movie);
        assertEquals("Inception", movie.getTitle());
        assertEquals("A mind-bending thriller", movie.getDescription());
        assertEquals("Sci-Fi", movie.getGenre());
        assertEquals("Christopher", movie.getDirector().getFirstName());
        assertEquals("Nolan", movie.getDirector().getLastName());
        assertEquals(2010, movie.getReleaseYear());
        assertEquals(148, movie.getDuration());
        assertEquals("English", movie.getLanguage());
        assertEquals(new BigDecimal("160000000"), movie.getBudget());
        assertEquals(new BigDecimal("830000000"), movie.getBoxOffice());
        assertNotNull(movie.getCreatedAt());
        assertNotNull(movie.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Movie movie = new Movie();
        movie.setTitle("The Dark Knight");
        movie.setDescription("A Batman movie");
        movie.setGenre("Action");
        movie.setRating(new BigDecimal("9.0"));
        movie.setReleaseYear(2008);
        movie.setDuration(152);
        movie.setLanguage("English");
        movie.setBudget(new BigDecimal("185000000"));
        movie.setBoxOffice(new BigDecimal("1000000000"));

        assertEquals("The Dark Knight", movie.getTitle());
        assertEquals("A Batman movie", movie.getDescription());
        assertEquals("Action", movie.getGenre());
        assertEquals(new BigDecimal("9.0"), movie.getRating());
        assertEquals(2008, movie.getReleaseYear());
        assertEquals(152, movie.getDuration());
        assertEquals("English", movie.getLanguage());
        assertEquals(new BigDecimal("185000000"), movie.getBudget());
        assertEquals(new BigDecimal("1000000000"), movie.getBoxOffice());
    }

    @Test
    void testTimestamps() {
        Movie movie = new Movie();
        Instant createdAtBeforeSave = movie.getCreatedAt();

        movie.setUpdatedAt(Instant.now());

        assertNotNull(movie.getUpdatedAt());
        assertNotEquals(createdAtBeforeSave, movie.getUpdatedAt());
    }

    @Test
    void testDefaultConstructor() {
        Movie movie = new Movie();

        assertNotNull(movie);
        assertNull(movie.getId());
        assertNull(movie.getTitle());
        assertNull(movie.getDescription());
        assertNull(movie.getGenre());
        assertNull(movie.getRating());
        assertEquals(0, movie.getReleaseYear());
        assertEquals(0, movie.getDuration());
        assertNull(movie.getLanguage());
        assertNull(movie.getBudget());
        assertNull(movie.getBoxOffice());
        assertNull(movie.getCreatedAt());
        assertNull(movie.getUpdatedAt());
    }
}
