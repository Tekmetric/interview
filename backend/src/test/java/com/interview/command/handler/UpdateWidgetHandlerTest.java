package com.interview.command.handler;

import com.interview.command.dto.UpdateWidgetCommand;
import com.interview.command.mapper.WidgetCommandMapper;
import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.entity.Widget;
import com.interview.common.events.WidgetUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateWidgetHandlerTest {

    @Mock
    private WidgetCommandRepository widgetCommandRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private WidgetCommandMapper mapper;

    @InjectMocks
    private UpdateWidgetHandler updateWidgetHandler;

    private UpdateWidgetCommand command;
    private Widget existingWidget;
    private Widget updatedWidget;
    private WidgetUpdatedEvent event;

    @BeforeEach
    void setUp() {
        command = new UpdateWidgetCommand("Updated Widget", "Updated Description");
        existingWidget = new Widget(1L, "Original Widget", "Original Description");
        updatedWidget = new Widget(1L, "Updated Widget", "Updated Description");
        event = new WidgetUpdatedEvent(1L, "Updated Widget", "Updated Description");
    }

    @Test
    void handle_ShouldUpdateWidget_WhenWidgetExists() {
        // Arrange
        when(widgetCommandRepository.findById(1L)).thenReturn(Optional.of(existingWidget));
        when(widgetCommandRepository.save(existingWidget)).thenReturn(updatedWidget);
        when(mapper.toUpdatedEvent(updatedWidget)).thenReturn(event);

        // Act
        Optional<Widget> result = updateWidgetHandler.handle(1L, command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Widget", result.get().getName());
        assertEquals("Updated Description", result.get().getDescription());

        verify(widgetCommandRepository).findById(1L);
        verify(mapper).updateEntity(command, existingWidget);
        verify(widgetCommandRepository).save(existingWidget);
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void handle_ShouldReturnEmpty_WhenWidgetNotFound() {
        // Arrange
        when(widgetCommandRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Widget> result = updateWidgetHandler.handle(999L, command);

        // Assert
        assertFalse(result.isPresent());
        verify(widgetCommandRepository).findById(999L);
        verify(widgetCommandRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void handle_ShouldPublishEvent_AfterSuccessfulUpdate() {
        // Arrange
        when(widgetCommandRepository.findById(1L)).thenReturn(Optional.of(existingWidget));
        when(widgetCommandRepository.save(existingWidget)).thenReturn(updatedWidget);
        when(mapper.toUpdatedEvent(updatedWidget)).thenReturn(event);

        // Act
        updateWidgetHandler.handle(1L, command);

        // Assert - verify event is published after save
        verify(widgetCommandRepository).save(existingWidget);
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void handle_ShouldUseMapper_ToUpdateEntity() {
        // Arrange
        when(widgetCommandRepository.findById(1L)).thenReturn(Optional.of(existingWidget));
        when(widgetCommandRepository.save(existingWidget)).thenReturn(updatedWidget);
        when(mapper.toUpdatedEvent(updatedWidget)).thenReturn(event);

        // Act
        updateWidgetHandler.handle(1L, command);

        // Assert - verify mapper is used to update entity
        verify(mapper).updateEntity(command, existingWidget);
        verify(widgetCommandRepository).save(existingWidget);
    }

    @Test
    void handle_ShouldNotPublishEvent_WhenWidgetNotFound() {
        // Arrange
        when(widgetCommandRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        updateWidgetHandler.handle(999L, command);

        // Assert
        verify(eventPublisher, never()).publishEvent(any());
        verify(mapper, never()).toUpdatedEvent(any());
    }
}
