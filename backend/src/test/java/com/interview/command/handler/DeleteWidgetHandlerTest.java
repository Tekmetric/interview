package com.interview.command.handler;

import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.entity.Widget;
import com.interview.common.events.WidgetDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteWidgetHandlerTest {

    @Mock
    private WidgetCommandRepository widgetCommandRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeleteWidgetHandler deleteWidgetHandler;

    private Widget existingWidget;

    @BeforeEach
    void setUp() {
        existingWidget = new Widget(1L, "Test Widget", "Test Description");
    }

    @Test
    void handle_ShouldDeleteWidget_WhenWidgetExists() {
        // Arrange
        when(widgetCommandRepository.findById(1L)).thenReturn(Optional.of(existingWidget));

        // Act
        boolean result = deleteWidgetHandler.handle(1L);

        // Assert
        assertTrue(result);
        verify(widgetCommandRepository).findById(1L);
        verify(widgetCommandRepository).delete(existingWidget);
        verify(eventPublisher).publishEvent(any(WidgetDeletedEvent.class));
    }

    @Test
    void handle_ShouldReturnFalse_WhenWidgetNotFound() {
        // Arrange
        when(widgetCommandRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        boolean result = deleteWidgetHandler.handle(999L);

        // Assert
        assertFalse(result);
        verify(widgetCommandRepository).findById(999L);
        verify(widgetCommandRepository, never()).delete(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void handle_ShouldPublishEvent_AfterSuccessfulDeletion() {
        // Arrange
        when(widgetCommandRepository.findById(1L)).thenReturn(Optional.of(existingWidget));

        // Act
        deleteWidgetHandler.handle(1L);

        // Assert - verify event is published after delete
        verify(widgetCommandRepository).delete(existingWidget);
        verify(eventPublisher).publishEvent(any(WidgetDeletedEvent.class));
    }

    @Test
    void handle_ShouldPublishCorrectEvent_WithWidgetId() {
        // Arrange
        when(widgetCommandRepository.findById(1L)).thenReturn(Optional.of(existingWidget));

        // Act
        deleteWidgetHandler.handle(1L);

        // Assert - verify correct event is published with widget ID
        verify(eventPublisher).publishEvent(argThat((Object event) ->
            event instanceof WidgetDeletedEvent &&
            ((WidgetDeletedEvent) event).getId() == 1L
        ));
    }

    @Test
    void handle_ShouldNotDeleteOrPublish_WhenWidgetNotFound() {
        // Arrange
        when(widgetCommandRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        boolean result = deleteWidgetHandler.handle(999L);

        // Assert
        assertFalse(result);
        verify(widgetCommandRepository, never()).delete(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
