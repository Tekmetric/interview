package com.interview.query.controller;

import com.interview.query.dto.WidgetDto;
import com.interview.query.handler.GetAllWidgetsHandler;
import com.interview.query.handler.GetWidgetByIdHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WidgetQueryController.class)
class WidgetQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAllWidgetsHandler getAllWidgetsHandler;

    @MockBean
    private GetWidgetByIdHandler getWidgetByIdHandler;

    @Test
    void getAllWidgets_ShouldReturnAllWidgets_WhenWidgetsExist() throws Exception {
        // Arrange
        WidgetDto widget1 = new WidgetDto(1L, "Widget 1", "Description 1");
        WidgetDto widget2 = new WidgetDto(2L, "Widget 2", "Description 2");
        when(getAllWidgetsHandler.handle()).thenReturn(Arrays.asList(widget1, widget2));

        // Act & Assert
        mockMvc.perform(get("/api/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Widget 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Widget 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"));
    }

    @Test
    void getAllWidgets_ShouldReturnEmptyArray_WhenNoWidgetsExist() throws Exception {
        // Arrange
        when(getAllWidgetsHandler.handle()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getWidgetById_ShouldReturnWidget_WhenWidgetExists() throws Exception {
        // Arrange
        WidgetDto widget = new WidgetDto(1L, "Test Widget", "Test Description");
        when(getWidgetByIdHandler.handle(1L)).thenReturn(Optional.of(widget));

        // Act & Assert
        mockMvc.perform(get("/api/widgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Widget"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void getWidgetById_ShouldReturnNotFound_WhenWidgetDoesNotExist() throws Exception {
        // Arrange
        when(getWidgetByIdHandler.handle(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/widgets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWidgetById_ShouldHandleDifferentIds() throws Exception {
        // Arrange
        WidgetDto widget5 = new WidgetDto(5L, "Widget 5", "Description 5");
        WidgetDto widget10 = new WidgetDto(10L, "Widget 10", "Description 10");

        when(getWidgetByIdHandler.handle(5L)).thenReturn(Optional.of(widget5));
        when(getWidgetByIdHandler.handle(10L)).thenReturn(Optional.of(widget10));

        // Act & Assert
        mockMvc.perform(get("/api/widgets/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Widget 5"));

        mockMvc.perform(get("/api/widgets/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Widget 10"));
    }
}
