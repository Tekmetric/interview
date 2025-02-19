package com.interview.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.interview.models.Movie;

public interface IMovieService {
    public Page<Movie> getMovies(final Pageable pageable);

    public Movie getMovieById(final long id);

    public void deleteMovieById(final long id);

    public Movie saveMovie(final Movie movie);

    public Movie updateMovie(final long id, final Movie movie);

    public Page<Movie> getMoviesByGenre(final String genre, final Pageable pageable);

    public Page<Movie> getMoviesByActor(final String firstName, final String lastName, final Pageable pageable);

    public Page<Movie> getMoviesByKeyword(final String keyword, final Pageable pageable);

    public Page<Movie> getMoviesByLanguage(final String language, final Pageable pageable);

    public Page<Movie> getMoviesByDirector(final String director, final Pageable pageable);

    public Page<Movie> getMoviesByReleaseYear(final int releaseYear, final Pageable pageable);

    public Page<Movie> getMoviesByRating(final double rating, final Pageable pageable);
}
