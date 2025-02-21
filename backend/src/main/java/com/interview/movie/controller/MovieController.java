package com.interview.movie.controller;

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
import com.interview.shared.util.ConvertUtil;

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
     * Create movie
     * 
     * @param movie
     * @return
     */
    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movie) {
        Movie movieEntity = new Movie(movie);
        Movie savedMovie = movieService.createMovie(movieEntity);

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
     * Get movies by filters
     * 
     * @param firstName
     * @param lastName
     * @param pageable
     * @return
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<MovieDTO>> getMoviesByFilters(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String actorFirstName,
            @RequestParam(required = false) String actorLastName,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String directorFirstName,
            @RequestParam(required = false) String directorLastName,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) BigDecimal rating,
            Pageable pageable) {

        Page<Movie> result = movieService.getMoviesByFilter(genre, actorFirstName, actorLastName, keyword, language,
                directorFirstName, directorLastName, releaseYear, rating, pageable);

        return ResponseEntity.ok(result.map(movie -> ConvertUtil.convertToDTO(movie, MovieDTO.class)));
    }

}