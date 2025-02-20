package com.interview.movie.controller;

import com.interview.util.ConvertUtil;
import jakarta.validation.Valid;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interview.movie.dto.MovieDTO;
import com.interview.movie.model.Movie;
import com.interview.movie.service.MovieService;

@RestController
@Validated
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(final MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Get all movies, paged
     * 
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getMoviesPaged(final Pageable pageable) {
        return ResponseEntity
                .ok(movieService.getMovies(pageable).map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

    /**
     * Get movie by id
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable("id") long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ConvertUtil.convertToDTO(movie, MovieDTO.class));
    }

    /**
     * Save movie
     * 
     * @param movie
     * @return
     */
    @PostMapping
    public ResponseEntity<MovieDTO> saveMovie(@Valid @RequestBody MovieDTO movie) {
        Movie movieEntity = new Movie(movie);
        Movie savedMovie = movieService.saveMovie(movieEntity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ConvertUtil.convertToDTO(savedMovie, MovieDTO.class));
    }

    /**
     * Delete movie by id
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable("id") long id) {
        movieService.deleteMovieById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update movie
     * 
     * @param id
     * @param movie
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable("id") long id, @Valid @RequestBody final MovieDTO movie) {
        Movie mov = movieService.updateMovie(id, new Movie(movie));
        return ResponseEntity
                .ok(ConvertUtil.convertToDTO(mov, MovieDTO.class));
    }

    /**
     * Get movies by actor firstName and lastName
     * 
     * @param firstName
     * @param lastName
     * @param pageable
     * @return
     */
    @GetMapping("/filter/actor")
    public ResponseEntity<Page<MovieDTO>> getMoviesByActor(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getMoviesByActor(firstName, lastName, pageable)
                .map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

    /**
     * Get movies by keyword
     * 
     * @param value
     * @param pageable
     * @return
     */
    @GetMapping("/filter/keyword")
    public ResponseEntity<Page<MovieDTO>> getMoviesByKeyword(
            @RequestParam(required = false) String value,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getMoviesByKeyword(
                value, pageable)
                .map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

    /**
     * Get movies by language
     * 
     * @param value
     * @param pageable
     * @return
     */
    @GetMapping("/filter/language")
    public ResponseEntity<Page<MovieDTO>> getMoviesByLanguage(
            @RequestParam(required = false) String value,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getMoviesByLanguage(
                value, pageable)
                .map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

    /**
     * Get movies by director firstName and lastName
     * 
     * @param firstName
     * @param lastName
     * @param pageable
     * @return
     */
    @GetMapping("/filter/director")
    public ResponseEntity<Page<MovieDTO>> getMoviesByDirector(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getMoviesByDirector(firstName, lastName, pageable)
                .map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

    /**
     * Get movies by release year
     * 
     * @param value
     * @param pageable
     * @return
     */
    @GetMapping("/filter/release-year")
    public ResponseEntity<Page<MovieDTO>> getMoviesByReleaseYear(
            @RequestParam(required = false) Integer value,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getMoviesByReleaseYear(
                value, pageable)
                .map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

    /**
     * Get movies by minimum rating
     * 
     * @param value
     * @param pageable
     * @return
     */
    @GetMapping("/filter/rating")
    public ResponseEntity<Page<MovieDTO>> getMoviesByMinRating(
            @RequestParam(required = false) BigDecimal value,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getMoviesByMinRating(
                value, pageable)
                .map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

    /**
     * Get movies by genre
     * 
     * @param value
     * @param pageable
     * @return
     */
    @GetMapping("/filter/genre")
    public ResponseEntity<Page<MovieDTO>> getMoviesByGenre(
            @RequestParam(required = false) String value,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getMoviesByGenre(value, pageable)
                .map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

}
