package com.interview.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.interview.dto.DirectorDTO;
import com.interview.dto.MovieDTO;
import com.interview.models.Director;
import com.interview.models.Movie;

public class ConvertUtilTest {
    @Test
    public void testConvertMovieToMovieDTO() {
        Movie movieDTO = new Movie();
        movieDTO.setTitle("Valid Movie");
        movieDTO.setGenre("Drama");
        movieDTO.setRating(new BigDecimal(7));
        movieDTO.setLanguage("English");
        movieDTO.setReleaseYear(2022);
        movieDTO.setDuration(120);
        movieDTO.setDirector(new Director());

        Class<MovieDTO> movieDTOClass = MovieDTO.class;

        MovieDTO movie = ConvertUtil.convertToDTO(movieDTO, movieDTOClass);

        assertEquals(movieDTO.getTitle(), movie.getTitle());
        assertEquals(movieDTO.getGenre(), movie.getGenre());
        assertEquals(movieDTO.getRating(), movie.getRating());
        assertEquals(movieDTO.getLanguage(), movie.getLanguage());
        assertEquals(movieDTO.getReleaseYear(), movie.getReleaseYear());
        assertEquals(movieDTO.getDuration(), movie.getDuration());

    }

    @Test
    public void testConvertDirectorToDirectorDTO() {
        Director director = new Director();
        director.setFirstName("John");
        director.setLastName("Doe");

        Class<DirectorDTO> directorDTOClass = DirectorDTO.class;

        DirectorDTO directorDTO = ConvertUtil.convertToDTO(director, directorDTOClass);

        assertEquals(director.getFirstName(), directorDTO.getFirstName());
        assertEquals(director.getLastName(), directorDTO.getLastName());
    }

    @Test()
    public void testErrorConvertingToInvalidDTO() {
        Movie movie = new Movie();
        Class<DirectorDTO> dtoClass = DirectorDTO.class;

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ConvertUtil.convertToDTO(movie, dtoClass);
        });

        assertTrue(exception.getMessage().contains("Error converting to DTO"));

    }
}
