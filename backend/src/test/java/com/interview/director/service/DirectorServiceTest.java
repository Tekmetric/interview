package com.interview.director.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.interview.director.model.Director;
import com.interview.director.repository.IDirectorRepository;
import com.interview.shared.exceptions.NotFoundException;
import com.interview.shared.exceptions.UniqueConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class DirectorServiceTest {
    @Mock
    private IDirectorRepository directorRepository;

    @InjectMocks
    private DirectorService directorService;

    private Director sampleDirector;

    @BeforeEach
    void setUp() {
        sampleDirector = new Director();
        sampleDirector.setFirstName("John");
        sampleDirector.setLastName("Doe");
    }

    @Test
    void testSaveDirector() {
        when(directorRepository.save(any(Director.class))).thenReturn(sampleDirector);

        Director savedDirector = directorService.createDirector(sampleDirector);

        assertNotNull(savedDirector);
        assertEquals("John", savedDirector.getFirstName());
        verify(directorRepository, times(1)).save(sampleDirector);
    }

    @Test
    void testSaveDirector_ThrowsUniqueConstraintViolationException() {
        when(directorRepository.findByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Optional.of(sampleDirector));

        UniqueConstraintViolationException exception = assertThrows(UniqueConstraintViolationException.class, () -> {
            directorService.createDirector(sampleDirector);
        });

        assertEquals("Director already exists", exception.getMessage());
        verify(directorRepository, times(1)).findByFirstNameAndLastName("John", "Doe");
    }

    @Test
    void testDeleteDirectorById() {
        when(directorRepository.findById(1L)).thenReturn(Optional.of(sampleDirector));

        directorService.deleteDirectorById(1L);

        verify(directorRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testDeleteDirectorById_ThrowsNotFoundException() {
        when(directorRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            directorService.deleteDirectorById(1L);
        });

        assertEquals("Director not found", exception.getMessage());
        verify(directorRepository, never()).delete(any(Director.class));
    }

    @Test
    void testUpdateDirector() {
        Director updatedDirector = new Director();
        updatedDirector.setFirstName("Jane");
        updatedDirector.setLastName("Doe");

        when(directorRepository.findById(1L)).thenReturn(Optional.of(sampleDirector));
        when(directorRepository.save(any(Director.class))).thenReturn(updatedDirector);

        Director result = directorService.updateDirector(1L, updatedDirector);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        verify(directorRepository, times(1)).save(sampleDirector);
    }

    @Test
    void testUpdateDirector_ThrowsNotFoundException() {
        when(directorRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            directorService.updateDirector(1L, sampleDirector);
        });

        assertEquals("Director not found", exception.getMessage());
        verify(directorRepository, never()).save(any(Director.class));
    }

    @Test
    void testGetDirectorById() {
        when(directorRepository.findById(1L)).thenReturn(Optional.of(sampleDirector));

        Director foundDirector = directorService.getDirectorById(1L);

        assertNotNull(foundDirector);
        assertEquals("John", foundDirector.getFirstName());
    }

    @Test
    void testGetDirectorById_ThrowsNotFoundException() {
        when(directorRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            directorService.getDirectorById(1L);
        });

        assertEquals("Director not found", exception.getMessage());
    }

    @Test
    void testGetDirectors() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Director> directorPage = new PageImpl<>(List.of(sampleDirector));

        when(directorRepository.findAll(pageable)).thenReturn(directorPage);

        Page<Director> result = directorService.getDirectors(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(directorRepository, times(1)).findAll(pageable);
    }

}
