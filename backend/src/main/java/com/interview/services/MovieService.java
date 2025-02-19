package com.interview.services;

import com.interview.interfaces.IMovieService;
import java.time.Instant;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.models.Movie;
import com.interview.repositories.IMovieRepository;

@Service
public class MovieService implements IMovieService {

    private IMovieRepository movieRepository;

    public MovieService(final IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Page<Movie> getMovies(final Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public Movie getMovieById(final long id) {
        return movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    @Transactional
    public void deleteMovieById(final long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));
        movieRepository.delete(movie);
    }

    @Transactional
    public Movie saveMovie(final Movie movie) {
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie updateMovie(final long id, final Movie movie) {
        Movie existingMovie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));
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
        existingMovie.setUpdatedAt(Instant.now());

        return existingMovie;
    }

    public Page<Movie> getMoviesByGenre(final String genre, final Pageable pageable) {
        return movieRepository.findByGenre(genre, pageable);
    }

    public Page<Movie> getMoviesByActor(final String firstName, final String lastName, final Pageable pageable) {

        return movieRepository.findByActorFirstNameLastName(firstName, lastName, pageable);
    }

    public Page<Movie> getMoviesByKeyword(final String keyword, final Pageable pageable) {
        return movieRepository.findByKeywords(keyword, pageable);
    }

    public Page<Movie> getMoviesByLanguage(final String language, final Pageable pageable) {
        return movieRepository.findByLanguage(language, pageable);
    }

    public Page<Movie> getMoviesByDirector(final String director, final Pageable pageable) {
        return movieRepository.findByDirector(director, pageable);
    }

    public Page<Movie> getMoviesByReleaseYear(final int releaseYear, final Pageable pageable) {
        return movieRepository.findByReleaseYear(releaseYear, pageable);
    }

    public Page<Movie> getMoviesByRating(final double rating, final Pageable pageable) {
        return movieRepository.findByRating(rating, pageable);
    }

}
