package com.interview.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.interview.models.Actor;
import com.interview.models.Director;
import com.interview.models.Keyword;
import com.interview.models.Movie;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class MovieDTOTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidations() {
        MovieDTO movie = new MovieDTO();

        Set<ConstraintViolation<MovieDTO>> violations = validator.validate(movie);
        assertFalse(violations.isEmpty());

        Set<String> validationMessages = Set.of("Title is required", "Genre is required",
                "Rating must be greater than 0", "Duration must be greater than 0", "Rating must be less than 10",
                "Language is required", "Director is required");

        violations.stream().forEach(violation -> {
            String message = violation.getMessage();
            assertEquals(true, validationMessages.contains(message));
        });

    }

    @Test
    void testValidMovieDTO() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Valid Movie");
        movieDTO.setGenre("Drama");
        movieDTO.setRating(new BigDecimal(7));
        movieDTO.setLanguage("English");
        movieDTO.setReleaseYear(2022);
        movieDTO.setDuration(120);
        movieDTO.setDirector(new DirectorDTO());

        Set<ConstraintViolation<MovieDTO>> violations = validator.validate(movieDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testMappingFromMovie() {
        Movie movie = new Movie();
        movie.setTitle("Valid Movie");
        movie.setGenre("Drama");
        movie.setRating(new BigDecimal(7));
        movie.setLanguage("English");
        movie.setReleaseYear(2022);
        movie.setDuration(120);

        Director director = new Director();
        director.setId(1L);

        movie.setDirector(director);

        List<Actor> actors = List.of(new Actor(), new Actor());
        movie.setActors(actors);

        List<Keyword> keywords = List.of(new Keyword(), new Keyword());
        movie.setKeywords(keywords);

        MovieDTO movieDTO = new MovieDTO(movie);

        assertEquals(movieDTO.getTitle(), movie.getTitle());
        assertEquals(movieDTO.getGenre(), movie.getGenre());
        assertEquals(movieDTO.getRating(), movie.getRating());
        assertEquals(movieDTO.getLanguage(), movie.getLanguage());
        assertEquals(movieDTO.getReleaseYear(), movie.getReleaseYear());
        assertEquals(movieDTO.getDuration(), movie.getDuration());
        assertEquals(movieDTO.getDirector().getId(), movie.getDirector().getId());
        assertEquals(movieDTO.getActors().size(), movie.getActors().size());
        assertEquals(movieDTO.getKeywords().size(), movie.getKeywords().size());
    }

    @Test
    void testAllGettersAndSetters() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Valid Movie");
        movieDTO.setGenre("Drama");
        movieDTO.setRating(new BigDecimal(7));
        movieDTO.setLanguage("English");
        movieDTO.setReleaseYear(2022);
        movieDTO.setDuration(120);
        movieDTO.setCreatedAt(null);
        movieDTO.setUpdatedAt(null);
        movieDTO.setDescription("description");
        movieDTO.setKeywords(List.of(new KeywordDTO()));
        movieDTO.setActors(List.of(new ActorDTO()));
        movieDTO.setBudget(new BigDecimal(1000));
        movieDTO.setBoxOffice(new BigDecimal(2000));

        assertEquals(movieDTO.getTitle(), "Valid Movie");
        assertEquals(movieDTO.getGenre(), "Drama");
        assertEquals(movieDTO.getRating(), new BigDecimal(7));
        assertEquals(movieDTO.getLanguage(), "English");
        assertEquals(movieDTO.getReleaseYear(), 2022);
        assertEquals(movieDTO.getDuration(), 120);
        assertEquals(movieDTO.getDescription(), "description");
        assertEquals(movieDTO.getCreatedAt(), null);
        assertEquals(movieDTO.getUpdatedAt(), null);
        assertEquals(movieDTO.getKeywords().size(), List.of(new KeywordDTO()).size());
        assertEquals(movieDTO.getActors().size(), List.of(new ActorDTO()).size());
        assertEquals(movieDTO.getBudget(), new BigDecimal(1000));
        assertEquals(movieDTO.getBoxOffice(), new BigDecimal(2000));
    }

    @Test
    void testNegativeRating() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Valid Movie");
        movieDTO.setGenre("Drama");
        movieDTO.setRating(new BigDecimal(-10));
        movieDTO.setLanguage("English");
        movieDTO.setReleaseYear(2022);
        movieDTO.setDuration(120);
        movieDTO.setDirector(new DirectorDTO());

        Set<ConstraintViolation<MovieDTO>> violations = validator.validate(movieDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRatingGreaterThan10() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Valid Movie");
        movieDTO.setGenre("Drama");
        movieDTO.setRating(new BigDecimal(11));
        movieDTO.setLanguage("English");
        movieDTO.setReleaseYear(2022);
        movieDTO.setDuration(120);
        movieDTO.setDirector(new DirectorDTO());

        Set<ConstraintViolation<MovieDTO>> violations = validator.validate(movieDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNegativeDuration() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Valid Movie");
        movieDTO.setGenre("Drama");
        movieDTO.setRating(new BigDecimal(7));
        movieDTO.setLanguage("English");
        movieDTO.setReleaseYear(2022);
        movieDTO.setDuration(-10);
        movieDTO.setDirector(new DirectorDTO());

        Set<ConstraintViolation<MovieDTO>> violations = validator.validate(movieDTO);
        assertFalse(violations.isEmpty());
    }

}
