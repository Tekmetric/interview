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
import org.springframework.stereotype.Service;

import com.interview.movie.model.Movie;
import com.interview.movie.repository.IMovieRepository;

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
    public Movie saveMovie(final Movie movie) {

        movieRepository.findByTitle(movie.getTitle()).ifPresent(m -> {
            throw new UniqueConstraintViolationException("Movie already exists");
        });

        Director director = movie.getDirector();

        if (director.getId() == null) {
            director = directorService.saveDirector(director);
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

    public Page<Movie> getMoviesByGenre(final String genre, final Pageable pageable) {
        return movieRepository.findByGenre(genre, pageable);
    }

    public Page<Movie> getMoviesByActor(final String firstName, final String lastName, final Pageable pageable) {
        return movieRepository.findByActor(firstName, lastName, pageable);
    }

    public Page<Movie> getMoviesByKeyword(final String keyword, final Pageable pageable) {
        return movieRepository.findByKeyword(keyword, pageable);
    }

    public Page<Movie> getMoviesByLanguage(final String language, final Pageable pageable) {
        return movieRepository.findByLanguage(language, pageable);
    }

    public Page<Movie> getMoviesByDirector(final String firstName, final String lastName, final Pageable pageable) {
        return movieRepository.findByDirectorFirstNameLastName(firstName, lastName, pageable);
    }

    public Page<Movie> getMoviesByReleaseYear(final int releaseYear, final Pageable pageable) {
        return movieRepository.findByReleaseYear(releaseYear, pageable);
    }

    public Page<Movie> getMoviesByMinRating(final BigDecimal rating, final Pageable pageable) {
        return movieRepository.findByMinRating(rating, pageable);
    }

}
