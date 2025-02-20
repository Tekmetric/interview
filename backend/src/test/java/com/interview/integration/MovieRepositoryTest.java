package com.interview.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.interview.models.Actor;
import com.interview.models.Director;
import com.interview.models.Movie;
import com.interview.repositories.IActorRepository;
import com.interview.repositories.IDirectorRepository;
import com.interview.repositories.IMovieRepository;

import jakarta.transaction.Transactional;

@DataJpaTest
public class MovieRepositoryTest {
    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private IDirectorRepository directorRepository;

    @Autowired
    IActorRepository actorRepository;

    @Test
    @Transactional
    void testFindById() {
        Movie movie = new Movie();
        movie.setTitle("Test By Id Movie");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);

        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);

        movieRepository.save(movie);

        Optional<Movie> foundMovie = movieRepository.findById(movie.getId());

        assertTrue(foundMovie.isPresent());
        assertEquals("Test By Id Movie", foundMovie.get().getTitle());
    }

    @Test
    void testFindByGenre() {
        Movie movie = new Movie();
        movie.setTitle("New Genre Movie");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);
        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);

        movieRepository.save(movie);

        Page<Movie> movies = movieRepository.findByGenre("Sci-Fi", PageRequest.of(0, 10));

        assertNotNull(movies);
        assertTrue(movies.getTotalElements() > 0);
    }

    @Test
    @Transactional
    void testFindByActor() {
        Movie movie = new Movie();
        movie.setTitle("By Actor Movie");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);
        Actor actor = actorRepository.findById(1L).orElse(null);
        movie.setActors(List.of(actor));

        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);

        movieRepository.save(movie);

        Page<Movie> movies = movieRepository.findByActor("Leonardo", "DiCaprio", PageRequest.of(0, 10));

        assertNotNull(movies);
        assertTrue(movies.getTotalElements() > 0);
    }

    @Test
    @Transactional
    void testFindByKeyword() {
        Movie movie = new Movie();
        movie.setTitle("By Keyword Movie");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);

        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);

        movieRepository.save(movie);

        Page<Movie> movies = movieRepository.findByKeyword("Action", PageRequest.of(0, 10));

        assertNotNull(movies);
        assertEquals(0, movies.getTotalElements()); // Assuming no matching keyword
    }

    @Test
    @Transactional
    void testFindByLanguage() {
        Movie movie = new Movie();
        movie.setTitle("By Language Movie");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);
        movie.setLanguage("French");

        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);

        movieRepository.save(movie);

        Page<Movie> movies = movieRepository.findByLanguage("French", PageRequest.of(0, 10));

        assertNotNull(movies);
        assertTrue(movies.getTotalElements() > 0);
    }

    @Test
    @Transactional
    void testFindByDirectorName() {

        Movie movie = new Movie();
        movie.setTitle("By Director");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);
        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);
        movieRepository.save(movie);

        Page<Movie> movies = movieRepository.findByDirectorFirstNameLastName("Christopher", "Nolan",
                PageRequest.of(0, 10));

        assertNotNull(movies);
        assertTrue(movies.getTotalElements() > 0);
    }

    @Test
    @Transactional
    void testFindByReleaseYear() {
        Movie movie = new Movie();
        movie.setTitle("By Release Year Movie");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);

        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);

        movieRepository.save(movie);

        Page<Movie> movies = movieRepository.findByReleaseYear(2010, PageRequest.of(0, 10));

        assertNotNull(movies);
        assertTrue(movies.getTotalElements() > 0);
    }

    @Test
    @Transactional
    void testFindByMinRating() {
        Movie movie = new Movie();
        movie.setTitle("By Min Rating");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);

        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);
        movieRepository.save(movie);

        Page<Movie> movies = movieRepository.findByMinRating(new BigDecimal("8.0"), PageRequest.of(0, 10));

        assertNotNull(movies);
        assertTrue(movies.getTotalElements() > 0);
    }
}
