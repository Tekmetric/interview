package com.interview.controllers;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interview.dtos.MovieDTO;
import com.interview.models.Movie;
import com.interview.services.MovieService;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(final MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getMoviesPaged(final Pageable pageable) {
        return ResponseEntity.ok(movieService.getMovies(pageable).map(MovieDTO::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable("id") long id) {

        Movie movie = movieService.getMovieById(id);

        if (movie == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new MovieDTO(movie));
    }

    @PostMapping
    public ResponseEntity<MovieDTO> saveMovie(@RequestBody MovieDTO movie) {
        return ResponseEntity.ok(new MovieDTO(movieService.saveMovie(new Movie(movie))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable("id") long id) {
        movieService.deleteMovieById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable("id") long id, final MovieDTO movie) {
        return ResponseEntity.ok(new MovieDTO(movieService.updateMovie(id, new Movie(movie))));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<MovieDTO>> getMoviesByFilter(
            @RequestParam("filterType") String filterType,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) BigDecimal rating,
            @RequestParam(required = false) String genre,
            Pageable pageable) {

        Page<Movie> movies;

        switch (filterType.toLowerCase()) {
            case "actor":
                movies = movieService.getMoviesByActor(firstName, lastName, pageable);
                break;
            case "keyword":
                movies = movieService.getMoviesByKeyword(keyword, pageable);
                break;
            case "language":
                movies = movieService.getMoviesByLanguage(language, pageable);
                break;
            case "director":
                movies = movieService.getMoviesByDirector(firstName, lastName, pageable);
                break;
            case "release-year":
                movies = movieService.getMoviesByReleaseYear(releaseYear, pageable);
                break;
            case "rating":
                movies = movieService.getMoviesByMinRating(rating, pageable);
                break;
            case "genre":
                movies = movieService.getMoviesByGenre(genre, pageable);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(movies.map(this::convertToDTO));
    }

    private MovieDTO convertToDTO(Movie movie) {
        return new MovieDTO(movie);
    }
}
