package com.interview.movie.service;

import com.interview.director.model.Director;
import com.interview.director.service.DirectorService;
import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;

import java.math.BigDecimal;
import jakarta.transaction.Transactional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.interview.movie.model.Movie;
import com.interview.movie.repository.IMovieRepository;
import com.interview.movie.repository.MovieSpecification;

@Service
public class MovieService {

    private IMovieRepository movieRepository;
    private DirectorService directorService;

    public MovieService(final IMovieRepository movieRepository, DirectorService directorService) {
        this.movieRepository = movieRepository;
        this.directorService = directorService;

    }

    @Cacheable(value = "moviesList", key = "'page:' + #pageable.pageNumber + '- size:' + #pageable.pageSize")
    public Page<Movie> getMovies(final Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public Movie getMovieById(final long id) {
        return movieRepository.findById(id).orElseThrow(() -> new NotFoundException("Movie not found"));
    }

    @Transactional
    @CacheEvict(value = "moviesList", allEntries = true)
    public void deleteMovieById(final long id) {
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }

    @Transactional
    @CacheEvict(value = "moviesList", allEntries = true)
    public Movie createMovie(final Movie movie) {

        movieRepository.findByTitle(movie.getTitle()).ifPresent(m -> {
            throw new UniqueConstraintViolationException("Movie already exists");
        });

        Director director = movie.getDirector();

        if (director.getId() == null) {
            director = directorService.createDirector(director);
        }

        directorService.getDirectorById(director.getId());

        return movieRepository.save(movie);
    }

    @Transactional
    @CacheEvict(value = "moviesList", allEntries = true)
    public Movie updateMovie(final long id, final Movie movie) {
        Movie existingMovie = getMovieById(id);
        existingMovie.setTitle(movie.getTitle());
        existingMovie.setDescription(movie.getDescription());
        existingMovie.setGenre(movie.getGenre());
        existingMovie.setRating(movie.getRating());
        existingMovie.setReleaseYear(movie.getReleaseYear());
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setDirector(movie.getDirector());
        existingMovie.setActors(movie.getActors());
        existingMovie.setKeywords(movie.getKeywords());
        existingMovie.setLanguage(movie.getLanguage());
        existingMovie.setBudget(movie.getBudget());
        existingMovie.setBoxOffice(movie.getBoxOffice());

        return movieRepository.save(existingMovie);
    }

    public Page<Movie> getMoviesByFilter(final String genre, final String actorFirstName, final String actorLastName,
            final String keyword, final String language, final String directorFirstName, final String directorLastName,
            final int releaseYear, final BigDecimal rating, final Pageable pageable) {

        Specification<Movie> spec = Specification.where(MovieSpecification.hasGenre(genre))
                .and(MovieSpecification.hasActor(actorFirstName, actorLastName))
                .and(MovieSpecification.hasKeyword(keyword))
                .and(MovieSpecification.hasLanguage(language))
                .and(MovieSpecification.hasDirector(directorFirstName, directorLastName))
                .and(MovieSpecification.hasReleaseYear(releaseYear))
                .and(MovieSpecification.hasMinRating(rating));

        return movieRepository.findAll(spec, pageable);
    }

}
