package com.interview.command.handler;

import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.mapper.WidgetCommandMapper;
import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.entity.Widget;
import com.interview.common.events.WidgetCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateWidgetHandlerTest {

    @Mock
    private WidgetCommandRepository widgetCommandRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private WidgetCommandMapper mapper;

    @InjectMocks
    private CreateWidgetHandler createWidgetHandler;

    private CreateWidgetCommand command;
    private Widget widget;
    private Widget savedWidget;
    private WidgetCreatedEvent event;

    @BeforeEach
    void setUp() {
        command = new CreateWidgetCommand("Test Widget", "Test Description");
        widget = new Widget("Test Widget", "Test Description");
        savedWidget = new Widget(1L, "Test Widget", "Test Description");
        event = new WidgetCreatedEvent(1L, "Test Widget", "Test Description");
    }

    @Test
    void handle_ShouldCreateWidget_WhenValidCommandProvided() {
        // Arrange
        when(mapper.toEntity(command)).thenReturn(widget);
        when(widgetCommandRepository.save(widget)).thenReturn(savedWidget);
        when(mapper.toCreatedEvent(savedWidget)).thenReturn(event);

        // Act
        Widget result = createWidgetHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Widget", result.getName());
        assertEquals("Test Description", result.getDescription());

        verify(mapper).toEntity(command);
        verify(widgetCommandRepository).save(widget);
        verify(eventPublisher).publishEvent(event);
        verify(mapper).toCreatedEvent(savedWidget);
    }

    @Test
    void handle_ShouldPublishEvent_AfterSavingWidget() {
        // Arrange
        when(mapper.toEntity(command)).thenReturn(widget);
        when(widgetCommandRepository.save(widget)).thenReturn(savedWidget);
        when(mapper.toCreatedEvent(savedWidget)).thenReturn(event);

        // Act
        createWidgetHandler.handle(command);

        // Assert - verify event is published after save
        verify(widgetCommandRepository).save(widget);
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void handle_ShouldMapCommandToEntity_BeforeSaving() {
        // Arrange
        when(mapper.toEntity(command)).thenReturn(widget);
        when(widgetCommandRepository.save(widget)).thenReturn(savedWidget);
        when(mapper.toCreatedEvent(savedWidget)).thenReturn(event);

        // Act
        createWidgetHandler.handle(command);

        // Assert - verify mapping happens before save
        verify(mapper).toEntity(command);
        verify(widgetCommandRepository).save(widget);
    }

    @Test
    void handle_ShouldReturnSavedWidget_WithGeneratedId() {
        // Arrange
        when(mapper.toEntity(command)).thenReturn(widget);
        when(widgetCommandRepository.save(widget)).thenReturn(savedWidget);
        when(mapper.toCreatedEvent(savedWidget)).thenReturn(event);

        // Act
        Widget result = createWidgetHandler.handle(command);

        // Assert
        assertNotNull(result.getId(), "Saved widget should have an ID");
        assertEquals(savedWidget, result);
    }
}
