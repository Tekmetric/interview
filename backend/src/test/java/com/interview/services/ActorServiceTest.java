package com.interview.services;

import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;
import com.interview.models.Actor;
import com.interview.repositories.IActorRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {

    @Mock
    private IActorRepository actorRepository;

    @InjectMocks
    private ActorService actorService;

    private Actor sampleActor;

    @BeforeEach
    void setUp() {
        sampleActor = new Actor();
        sampleActor.setFirstName("John");
        sampleActor.setLastName("Doe");
    }

    @Test
    void testSaveActor() {
        when(actorRepository.save(any(Actor.class))).thenReturn(sampleActor);

        Actor savedActor = actorService.saveActor(sampleActor);

        assertNotNull(savedActor);
        assertEquals("John", savedActor.getFirstName());
        verify(actorRepository, times(1)).save(sampleActor);
    }

    @Test
    void testSaveActor_ThrowsUniqueConstraintViolationException() {
        when(actorRepository.save(any(Actor.class))).thenThrow(DataIntegrityViolationException.class);

        UniqueConstraintViolationException exception = assertThrows(UniqueConstraintViolationException.class, () -> {
            actorService.saveActor(sampleActor);
        });

        assertEquals("Actor already exists", exception.getMessage());
        verify(actorRepository, times(1)).save(sampleActor);
    }

    @Test
    void testDeleteActorById() {
        when(actorRepository.findById(1L)).thenReturn(Optional.of(sampleActor));

        actorService.deleteActorById(1L);

        verify(actorRepository, times(1)).delete(sampleActor);
    }

    @Test
    void testDeleteActorById_ThrowsNotFoundException() {
        when(actorRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            actorService.deleteActorById(1L);
        });

        assertEquals("Actor not found", exception.getMessage());
        verify(actorRepository, never()).delete(any(Actor.class));
    }

    @Test
    void testUpdateActor() {
        Actor updatedActor = new Actor();
        updatedActor.setFirstName("Jane");
        updatedActor.setLastName("Doe");

        when(actorRepository.findById(1L)).thenReturn(Optional.of(sampleActor));
        when(actorRepository.save(any(Actor.class))).thenReturn(updatedActor);

        Actor result = actorService.updateActor(1L, updatedActor);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        verify(actorRepository, times(1)).save(sampleActor);
    }

    @Test
    void testUpdateActor_ThrowsNotFoundException() {
        when(actorRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            actorService.updateActor(1L, sampleActor);
        });

        assertEquals("Actor not found", exception.getMessage());
        verify(actorRepository, never()).save(any(Actor.class));
    }

    @Test
    void testGetActorById() {
        when(actorRepository.findById(1L)).thenReturn(Optional.of(sampleActor));

        Actor foundActor = actorService.getActorById(1L);

        assertNotNull(foundActor);
        assertEquals("John", foundActor.getFirstName());
    }

    @Test
    void testGetActorById_ThrowsNotFoundException() {
        when(actorRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            actorService.getActorById(1L);
        });

        assertEquals("Actor not found", exception.getMessage());
    }

    @Test
    void testGetActors() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Actor> actorPage = new PageImpl<>(List.of(sampleActor));

        when(actorRepository.findAll(pageable)).thenReturn(actorPage);

        Page<Actor> result = actorService.getActors(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(actorRepository, times(1)).findAll(pageable);
    }
}
