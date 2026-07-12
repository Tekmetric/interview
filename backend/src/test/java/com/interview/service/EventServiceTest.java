package com.interview.service;

import com.interview.model.Event;
import com.interview.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testEvent = new Event();
        testEvent.setId(testId);
        testEvent.setName("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setLocation("Test Location");
        testEvent.setEventDate(LocalDateTime.now().plusDays(7));
    }

    @Test
    void getEventById_WhenEventExists_ShouldReturnEvent() {
        // Arrange
        when(eventRepository.findByIdWithDetails(testId)).thenReturn(Optional.of(testEvent));

        // Act
        Optional<Event> result = eventService.getEventById(testId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testEvent.getName(), result.get().getName());
        verify(eventRepository, times(1)).findByIdWithDetails(testId);
    }

    @Test
    void getEventById_WhenEventDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(eventRepository.findByIdWithDetails(testId)).thenReturn(Optional.empty());

        // Act
        Optional<Event> result = eventService.getEventById(testId);

        // Assert
        assertFalse(result.isPresent());
        verify(eventRepository, times(1)).findByIdWithDetails(testId);
    }

    @Test
    void createEvent_ShouldSaveAndReturnEvent() {
        // Arrange
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        Event savedEvent = eventService.createEvent(testEvent);

        // Assert
        assertNotNull(savedEvent);
        assertEquals(testEvent.getName(), savedEvent.getName());
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void updateEvent_WhenEventExists_ShouldUpdateAndReturnEvent() {
        // Arrange
        Event updatedDetails = new Event();
        updatedDetails.setName("Updated Name");
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setLocation("Updated Location");
        updatedDetails.setEventDate(LocalDateTime.now().plusDays(14));

        when(eventRepository.findById(testId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        Event result = eventService.updateEvent(testId, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(eventRepository, times(1)).findById(testId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent_WhenEventDoesNotExist_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            eventService.updateEvent(testId, testEvent);
        });
        verify(eventRepository, times(1)).findById(testId);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void deleteEvent_WhenEventExists_ShouldDeleteEvent() {
        // Arrange
        when(eventRepository.findById(testId)).thenReturn(Optional.of(testEvent));
        doNothing().when(eventRepository).delete(testEvent);

        // Act
        eventService.deleteEvent(testId);

        // Assert
        verify(eventRepository, times(1)).findById(testId);
        verify(eventRepository, times(1)).delete(testEvent);
    }

    @Test
    void deleteEvent_WhenEventDoesNotExist_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            eventService.deleteEvent(testId);
        });
        verify(eventRepository, times(1)).findById(testId);
        verify(eventRepository, never()).delete(any(Event.class));
    }

    @Test
    void searchEventsByName_ShouldReturnMatchingEvents() {
        // Arrange
        String searchTerm = "Test";
        List<Event> expectedEvents = Arrays.asList(testEvent);
        when(eventRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(expectedEvents);

        // Act
        List<Event> result = eventService.searchEventsByName(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEvent.getName(), result.get(0).getName());
        verify(eventRepository, times(1)).findByNameContainingIgnoreCase(searchTerm);
    }

    @Test
    void getEventsByLocation_ShouldReturnEventsInLocation() {
        // Arrange
        String location = "Test Location";
        List<Event> expectedEvents = Arrays.asList(testEvent);
        when(eventRepository.findByLocation(location)).thenReturn(expectedEvents);

        // Act
        List<Event> result = eventService.getEventsByLocation(location);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(location, result.get(0).getLocation());
        verify(eventRepository, times(1)).findByLocation(location);
    }

    @Test
    void getUpcomingEvents_ShouldReturnFutureEvents() {
        // Arrange
        List<Event> expectedEvents = Arrays.asList(testEvent);
        when(eventRepository.findByEventDateAfter(any(LocalDateTime.class))).thenReturn(expectedEvents);

        // Act
        List<Event> result = eventService.getUpcomingEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findByEventDateAfter(any(LocalDateTime.class));
    }

    @Test
    void getEventsBetweenDates_ShouldReturnEventsInDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        List<Event> expectedEvents = Arrays.asList(testEvent);
        when(eventRepository.findByEventDateBetween(startDate, endDate)).thenReturn(expectedEvents);

        // Act
        List<Event> result = eventService.getEventsBetweenDates(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findByEventDateBetween(startDate, endDate);
    }
}
