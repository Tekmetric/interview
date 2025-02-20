package com.interview.services;

import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;
import com.interview.models.Director;
import com.interview.models.Movie;
import com.interview.repositories.IDirectorRepository;
import com.interview.repositories.IMovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

        Movie savedMovie = movieService.saveMovie(sampleMovie);

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
        when(directorService.saveDirector(any(Director.class))).thenReturn(savedDirector);
        when(directorService.getDirectorById(anyLong())).thenReturn(savedDirector);

        Movie savedMovie = movieService.saveMovie(sampleMovie);

        assertNotNull(savedMovie);
        assertNotNull(savedMovie.getDirector());
        verify(movieRepository, times(1)).save(sampleMovie);
        verify(directorService, times(1)).saveDirector(any(Director.class));
        verify(directorService, times(1)).getDirectorById(anyLong());
    }

    @Test
    void testSaveMovie_ThrowsUniqueConstraintViolationException() {
        when(movieRepository.save(any(Movie.class))).thenThrow(DataIntegrityViolationException.class);

        UniqueConstraintViolationException exception = assertThrows(
                UniqueConstraintViolationException.class,
                () -> movieService.saveMovie(sampleMovie));

        assertEquals("Movie already exists", exception.getMessage());
        verify(movieRepository, times(1)).save(sampleMovie);
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
    void testGetMoviesByGenre() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findByGenre("Sci-Fi", pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getMoviesByGenre("Sci-Fi", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository, times(1)).findByGenre("Sci-Fi", pageable);
    }

    @Test
    void testGetMoviesByDirector() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findByDirectorFirstNameLastName("Christopher", "Nolan", pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getMoviesByDirector("Christopher", "Nolan", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository, times(1)).findByDirectorFirstNameLastName("Christopher", "Nolan", pageable);
    }

    @Test
    void testGetMoviesByActor() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findByActor("John", "Doe", pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getMoviesByActor("John", "Doe", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository, times(1)).findByActor(anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void testGetMoviesByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findByKeyword("keyword", pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getMoviesByKeyword("keyword", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository, times(1)).findByKeyword(anyString(), any(Pageable.class));
    }

    @Test
    void testGetMoviesByReleaseYear() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findByReleaseYear(2010, pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getMoviesByReleaseYear(2010, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository, times(1)).findByReleaseYear(2010, pageable);
    }

    @Test
    void getMoviesByMinRating() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findByMinRating(new BigDecimal(6), pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getMoviesByMinRating(new BigDecimal(6), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository, times(1)).findByMinRating(any(), any(Pageable.class));
    }

    @Test
    void getMoviesByLanguage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(sampleMovie));

        when(movieRepository.findByLanguage("English", pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getMoviesByLanguage("English", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository, times(1)).findByLanguage("English", pageable);
    }

}
