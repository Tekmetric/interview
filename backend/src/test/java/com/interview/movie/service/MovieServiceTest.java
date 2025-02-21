package com.interview.movie.service;

import com.interview.actor.model.Actor;
import com.interview.director.model.Director;
import com.interview.director.repository.IDirectorRepository;
import com.interview.director.service.DirectorService;
import com.interview.keyword.model.Keyword;
import com.interview.movie.model.Movie;
import com.interview.movie.repository.IMovieRepository;
import com.interview.shared.exceptions.NotFoundException;
import com.interview.shared.exceptions.UniqueConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private IMovieRepository movieRepository;

    @Mock
    private DirectorService directorService;

    @Mock
    private IDirectorRepository directorRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie sampleMovie;
    private Director sampleDirector;
    private Actor sampleActor;
    private Keyword sampleKeyword;

    @BeforeEach
    void setUp() {
        sampleDirector = new Director();
        sampleDirector.setId(1L);
        sampleDirector.setFirstName("Christopher");
        sampleDirector.setLastName("Nolan");

        sampleMovie = new Movie();
        sampleMovie.setLanguage("English");
        sampleMovie.setTitle("Inception");
        sampleMovie.setGenre("Sci-Fi");
        sampleMovie.setDirector(sampleDirector);
        sampleMovie.setReleaseYear(2010);
        sampleMovie.setDuration(148);
        sampleMovie.setRating(new BigDecimal(8));

        sampleActor = new Actor();
        sampleActor.setFirstName("John");
        sampleActor.setLastName("Doe");

        sampleKeyword = new Keyword();
        sampleKeyword.setName("keyword");

        sampleMovie.setActors(List.of(sampleActor));
        sampleMovie.setKeywords(List.of(sampleKeyword));
    }

    @Test
    void testGetMovies() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findAll(pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getMovies(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetMovieById() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleMovie));

        Movie result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
    }

    @Test
    void testGetMovieById_ThrowsNotFoundException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> movieService.getMovieById(1L));

        assertEquals("Movie not found", exception.getMessage());
    }

    @Test
    void testSaveMovie() {
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        Movie savedMovie = movieService.createMovie(sampleMovie);

        assertNotNull(savedMovie);
        assertEquals("Inception", savedMovie.getTitle());
        verify(movieRepository, times(1)).save(sampleMovie);
    }

    @Test
    void testSaveMovieWithNoDirector() {
        sampleMovie.getDirector().setId(null);

        Director savedDirector = new Director();
        savedDirector.setId(1L);

        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);
        when(directorService.createDirector(any(Director.class))).thenReturn(savedDirector);
        when(directorService.getDirectorById(anyLong())).thenReturn(savedDirector);

        Movie savedMovie = movieService.createMovie(sampleMovie);

        assertNotNull(savedMovie);
        assertNotNull(savedMovie.getDirector());
        verify(movieRepository, times(1)).save(sampleMovie);
        verify(directorService, times(1)).createDirector(any(Director.class));
        verify(directorService, times(1)).getDirectorById(anyLong());
    }

    @Test
    void testSaveMovie_ThrowsUniqueConstraintViolationException() {

        when(movieRepository.findByTitle(anyString()))
                .thenReturn(Optional.of(sampleMovie));

        UniqueConstraintViolationException exception = assertThrows(UniqueConstraintViolationException.class, () -> {
            movieService.createMovie(sampleMovie);
        });

        assertEquals("Movie already exists", exception.getMessage());
        verify(movieRepository, times(1)).findByTitle("Inception");

    }

    @Test
    void testDeleteMovieById() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleMovie));

        movieService.deleteMovieById(1L);

        verify(movieRepository, times(1)).delete(sampleMovie);
    }

    @Test
    void testDeleteMovieById_ThrowsNotFoundException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> movieService.deleteMovieById(1L));

        assertEquals("Movie not found", exception.getMessage());
        verify(movieRepository, never()).delete(any(Movie.class));
    }

    @Test
    void testUpdateMovie() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        Movie updatedMovie = new Movie();
        updatedMovie.setTitle("Interstellar");
        updatedMovie.setGenre("Sci-Fi");

        Movie result = movieService.updateMovie(1L, updatedMovie);

        assertNotNull(result);
        assertEquals("Interstellar", result.getTitle());
        verify(movieRepository, times(1)).save(sampleMovie);
    }

    @Test
    void testUpdateMovie_ThrowsNotFoundException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> movieService.updateMovie(1L, sampleMovie));

        assertEquals("Movie not found", exception.getMessage());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testGetMoviesFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findAll(ArgumentMatchers.<Specification<Movie>>any(), eq(pageable)))
                .thenReturn(moviePage);

        Page<Movie> result = movieService.getMoviesByFilter("Sci-Fi", sampleActor.getFirstName(),
                sampleActor.getLastName(), sampleKeyword.getName(), sampleMovie.getLanguage(),
                sampleDirector.getFirstName(), sampleDirector.getLastName(), sampleMovie.getReleaseYear(),
                sampleMovie.getRating(),
                pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(result.getContent().get(0), sampleMovie);
        verify(movieRepository, times(1)).findAll(ArgumentMatchers.<Specification<Movie>>any(), eq(pageable));
    }
}
