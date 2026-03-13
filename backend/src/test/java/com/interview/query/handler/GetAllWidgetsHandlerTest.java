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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllWidgetsHandlerTest {

    @Mock
    private WidgetQueryRepository widgetQueryRepository;

    @Mock
    private WidgetQueryMapper mapper;

    @InjectMocks
    private GetAllWidgetsHandler getAllWidgetsHandler;

    private List<Widget> widgets;
    private WidgetDto widgetDto1;
    private WidgetDto widgetDto2;

    @BeforeEach
    void setUp() {
        Widget widget1 = new Widget(1L, "Widget 1", "Description 1");
        Widget widget2 = new Widget(2L, "Widget 2", "Description 2");
        widgets = Arrays.asList(widget1, widget2);

        widgetDto1 = new WidgetDto(1L, "Widget 1", "Description 1");
        widgetDto2 = new WidgetDto(2L, "Widget 2", "Description 2");
    }

    @Test
    void handle_ShouldReturnAllWidgets_WhenWidgetsExist() {
        // Arrange
        when(widgetQueryRepository.findAll()).thenReturn(widgets);
        when(mapper.toDto(widgets.get(0))).thenReturn(widgetDto1);
        when(mapper.toDto(widgets.get(1))).thenReturn(widgetDto2);

        // Act
        List<WidgetDto> result = getAllWidgetsHandler.handle();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Widget 1", result.get(0).getName());
        assertEquals("Widget 2", result.get(1).getName());

        verify(widgetQueryRepository).findAll();
        verify(mapper, times(2)).toDto(any(Widget.class));
    }

    @Test
    void handle_ShouldReturnEmptyList_WhenNoWidgetsExist() {
        // Arrange
        when(widgetQueryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<WidgetDto> result = getAllWidgetsHandler.handle();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(widgetQueryRepository).findAll();
        verify(mapper, never()).toDto(any(Widget.class));
    }

    @Test
    void handle_ShouldMapAllEntities_ToDtos() {
        // Arrange
        when(widgetQueryRepository.findAll()).thenReturn(widgets);
        when(mapper.toDto(widgets.get(0))).thenReturn(widgetDto1);
        when(mapper.toDto(widgets.get(1))).thenReturn(widgetDto2);

        // Act
        List<WidgetDto> result = getAllWidgetsHandler.handle();

        // Assert
        verify(mapper).toDto(widgets.get(0));
        verify(mapper).toDto(widgets.get(1));
        assertEquals(2, result.size());
    }

    @Test
    void handle_ShouldReturnCorrectWidgetDetails() {
        // Arrange
        when(widgetQueryRepository.findAll()).thenReturn(widgets);
        when(mapper.toDto(widgets.get(0))).thenReturn(widgetDto1);
        when(mapper.toDto(widgets.get(1))).thenReturn(widgetDto2);

        // Act
        List<WidgetDto> result = getAllWidgetsHandler.handle();

        // Assert
        assertEquals(1L, result.get(0).getId());
        assertEquals("Widget 1", result.get(0).getName());
        assertEquals("Description 1", result.get(0).getDescription());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Widget 2", result.get(1).getName());
        assertEquals("Description 2", result.get(1).getDescription());
    }
}
