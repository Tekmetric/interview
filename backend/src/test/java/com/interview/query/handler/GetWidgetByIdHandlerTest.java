package com.interview.query.handler;

import com.interview.common.entity.Widget;
import com.interview.query.dto.WidgetDto;
import com.interview.query.mapper.WidgetQueryMapper;
import com.interview.query.repository.WidgetQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWidgetByIdHandlerTest {

    @Mock
    private WidgetQueryRepository widgetQueryRepository;

    @Mock
    private WidgetQueryMapper mapper;

    @InjectMocks
    private GetWidgetByIdHandler getWidgetByIdHandler;

    private Widget widget;
    private WidgetDto widgetDto;

    @BeforeEach
    void setUp() {
        widget = new Widget(1L, "Test Widget", "Test Description");
        widgetDto = new WidgetDto(1L, "Test Widget", "Test Description");
    }

    @Test
    void handle_ShouldReturnWidget_WhenWidgetExists() {
        // Arrange
        when(widgetQueryRepository.findById(1L)).thenReturn(Optional.of(widget));
        when(mapper.toDto(widget)).thenReturn(widgetDto);

        // Act
        Optional<WidgetDto> result = getWidgetByIdHandler.handle(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Widget", result.get().getName());
        assertEquals("Test Description", result.get().getDescription());

        verify(widgetQueryRepository).findById(1L);
        verify(mapper).toDto(widget);
    }

    @Test
    void handle_ShouldReturnEmpty_WhenWidgetNotFound() {
        // Arrange
        when(widgetQueryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<WidgetDto> result = getWidgetByIdHandler.handle(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(widgetQueryRepository).findById(999L);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void handle_ShouldMapEntity_ToDto_WhenWidgetFound() {
        // Arrange
        when(widgetQueryRepository.findById(1L)).thenReturn(Optional.of(widget));
        when(mapper.toDto(widget)).thenReturn(widgetDto);

        // Act
        Optional<WidgetDto> result = getWidgetByIdHandler.handle(1L);

        // Assert
        assertTrue(result.isPresent());
        verify(mapper).toDto(widget);
    }

    @Test
    void handle_ShouldNotMapEntity_WhenWidgetNotFound() {
        // Arrange
        when(widgetQueryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<WidgetDto> result = getWidgetByIdHandler.handle(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void handle_ShouldReturnCorrectWidgetDetails() {
        // Arrange
        when(widgetQueryRepository.findById(1L)).thenReturn(Optional.of(widget));
        when(mapper.toDto(widget)).thenReturn(widgetDto);

        // Act
        Optional<WidgetDto> result = getWidgetByIdHandler.handle(1L);

        // Assert
        assertTrue(result.isPresent());
        WidgetDto dto = result.get();
        assertEquals(widget.getId(), dto.getId());
        assertEquals(widget.getName(), dto.getName());
        assertEquals(widget.getDescription(), dto.getDescription());
    }
}
