package com.interview.controllers;

import com.interview.util.ConvertUtil;
import jakarta.validation.Valid;
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

import com.interview.dto.MovieDTO;
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
        return ResponseEntity
                .ok(movieService.getMovies(pageable).map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable("id") long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ConvertUtil.convertToDTO(movie, MovieDTO.class));
    }

    @PostMapping
    public ResponseEntity<MovieDTO> saveMovie(@Valid @RequestBody MovieDTO movie) {
        return ResponseEntity.ok(ConvertUtil.convertToDTO(movieService.saveMovie(new Movie(movie)), MovieDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable("id") long id) {
        movieService.deleteMovieById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable("id") long id, final MovieDTO movie) {
        return ResponseEntity
                .ok(ConvertUtil.convertToDTO(movieService.updateMovie(id, new Movie(movie)), MovieDTO.class));
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

        return ResponseEntity.ok(movies.map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }
}
