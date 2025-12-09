package com.interview.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.dto.UpdateWidgetCommand;
import com.interview.command.handler.CreateWidgetHandler;
import com.interview.command.handler.DeleteWidgetHandler;
import com.interview.command.handler.UpdateWidgetHandler;
import com.interview.common.entity.Widget;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WidgetCommandController.class)
class WidgetCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateWidgetHandler createWidgetHandler;

    @MockBean
    private UpdateWidgetHandler updateWidgetHandler;

    @MockBean
    private DeleteWidgetHandler deleteWidgetHandler;

    @Test
    void createWidget_ShouldReturnCreated_WhenValidCommand() throws Exception {
        // Arrange
        CreateWidgetCommand command = new CreateWidgetCommand("Test Widget", "Test Description");
        Widget createdWidget = new Widget(1L, "Test Widget", "Test Description");
        when(createWidgetHandler.handle(any(CreateWidgetCommand.class))).thenReturn(createdWidget);

        // Act & Assert
        mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Widget"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void createWidget_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        // Arrange
        CreateWidgetCommand command = new CreateWidgetCommand("", "Test Description");

        // Act & Assert
        mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWidget_ShouldReturnBadRequest_WhenNameIsTooLong() throws Exception {
        // Arrange
        String longName = "a".repeat(256);
        CreateWidgetCommand command = new CreateWidgetCommand(longName, "Test Description");

        // Act & Assert
        mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateWidget_ShouldReturnOk_WhenWidgetExists() throws Exception {
        // Arrange
        UpdateWidgetCommand command = new UpdateWidgetCommand("Updated Widget", "Updated Description");
        Widget updatedWidget = new Widget(1L, "Updated Widget", "Updated Description");
        when(updateWidgetHandler.handle(eq(1L), any(UpdateWidgetCommand.class)))
                .thenReturn(Optional.of(updatedWidget));

        // Act & Assert
        mockMvc.perform(put("/api/widgets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Widget"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void updateWidget_ShouldReturnNotFound_WhenWidgetDoesNotExist() throws Exception {
        // Arrange
        UpdateWidgetCommand command = new UpdateWidgetCommand("Updated Widget", "Updated Description");
        when(updateWidgetHandler.handle(eq(999L), any(UpdateWidgetCommand.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/widgets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateWidget_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        // Arrange
        UpdateWidgetCommand command = new UpdateWidgetCommand("", "Updated Description");

        // Act & Assert
        mockMvc.perform(put("/api/widgets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteWidget_ShouldReturnNoContent_WhenWidgetExists() throws Exception {
        // Arrange
        when(deleteWidgetHandler.handle(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/widgets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWidget_ShouldReturnNotFound_WhenWidgetDoesNotExist() throws Exception {
        // Arrange
        when(deleteWidgetHandler.handle(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/widgets/999"))
                .andExpect(status().isNotFound());
    }
}
