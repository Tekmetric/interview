package com.interview.movie.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.springframework.data.jpa.domain.Specification;

import com.interview.actor.model.Actor;
import com.interview.actor.repository.IActorRepository;
import com.interview.director.model.Director;
import com.interview.director.repository.IDirectorRepository;
import com.interview.keyword.model.Keyword;
import com.interview.keyword.repository.IKeywordRepository;
import com.interview.movie.model.Movie;

import jakarta.transaction.Transactional;

@DataJpaTest
public class MovieRepositoryTest {
    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private IDirectorRepository directorRepository;

    @Autowired
    private IKeywordRepository keywordRepository;

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
    @Transactional
    void testFindByFilter() {
        Movie movie = new Movie();
        movie.setTitle("New Genre Movie");
        movie.setLanguage("English");
        movie.setGenre("Sci-Fi");
        movie.setRating(new BigDecimal("8.8"));
        movie.setReleaseYear(2010);
        Director director = directorRepository.findById(1L).orElse(null);
        movie.setDirector(director);

        Keyword keyword = new Keyword();
        keyword.setName("Action");
        keywordRepository.save(keyword);

        movie.setKeywords(List.of(keyword));

        Actor actor = actorRepository.findById(1L).orElse(null);
        movie.setActors(List.of(actor));

        movieRepository.save(movie);

        Specification<Movie> spec = Specification.where(MovieSpecification.hasGenre("Sci-Fi"))
                .and(MovieSpecification.hasMinRating(new BigDecimal("8.8")))
                .and(MovieSpecification.hasReleaseYear(2010))
                .and(MovieSpecification.hasDirector("Christopher", "Nolan"))
                .and(MovieSpecification.hasLanguage("English"))
                .and(MovieSpecification.hasKeyword("Action"))
                .and(MovieSpecification.hasActor("Leonardo", "DiCaprio"));

        Page<Movie> movies = movieRepository.findAll(spec, PageRequest.of(0, 10));

        assertNotNull(movies);
        assertTrue(movies.getTotalElements() > 0);
        assertEquals(movies.getContent().get(0), movie);
    }

    @Test
    void testFindByFilterNoParams() {
        Specification<Movie> spec = Specification.where(MovieSpecification.hasGenre(null))
                .and(MovieSpecification.hasMinRating(null))
                .and(MovieSpecification.hasReleaseYear(null))
                .and(MovieSpecification.hasDirector(null, null))
                .and(MovieSpecification.hasLanguage(null))
                .and(MovieSpecification.hasKeyword(null))
                .and(MovieSpecification.hasActor(null, null));

        Page<Movie> movies = movieRepository.findAll(spec, PageRequest.of(0, 10));

        assertNotNull(movies);
        assertTrue(movies.getTotalElements() > 0);
    }
}
