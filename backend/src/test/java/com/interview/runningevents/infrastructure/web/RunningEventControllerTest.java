package com.interview.runningevents.infrastructure.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.domain.model.RunningEvent;
import com.interview.runningevents.infrastructure.web.dto.RunningEventDTOMapper;
import com.interview.runningevents.infrastructure.web.dto.RunningEventRequestDTO;

@WebMvcTest(RunningEventController.class)
@Import({RunningEventDTOMapper.class})
public class RunningEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateRunningEventUseCase createRunningEventUseCase;

    @MockBean
    private GetRunningEventUseCase getRunningEventUseCase;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    @BeforeEach
    public void setUp() {
        // Common setup code
    }

    @Test
    public void shouldCreateRunningEventSuccessfully() throws Exception {
        // Given
        Instant eventTime = Instant.now().plus(30, ChronoUnit.DAYS);
        String formattedTime = DATE_FORMATTER.format(eventTime);

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name("Test Marathon")
                .dateTime(eventTime.toEpochMilli())
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        RunningEvent createdEvent = RunningEvent.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(eventTime.toEpochMilli())
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        when(createRunningEventUseCase.createRunningEvent(any(RunningEvent.class)))
                .thenReturn(createdEvent);

        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/api/events/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Marathon")))
                .andExpect(jsonPath("$.dateTime", is(eventTime.toEpochMilli())))
                .andExpect(jsonPath("$.location", is("Test Location")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.furtherInformation", is("Further Information")))
                .andExpect(jsonPath("$.formattedDateTime", is(formattedTime)));
    }

    @Test
    public void shouldRejectRunningEventWithInvalidData() throws Exception {
        // Given
        RunningEventRequestDTO invalidDTO = RunningEventRequestDTO.builder()
                .name("") // Empty name - should fail validation
                .dateTime(null) // Null dateTime - should fail validation
                .location("Test Location")
                .build();

        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Validation failed. Check 'errors' field for details.")))
                .andExpect(jsonPath("$.errors", notNullValue()))
                .andExpect(jsonPath("$.errors.length()", is(2)))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].message").exists())
                .andExpect(
                        jsonPath("$.errors[?(@.field == 'dateTime')].message").exists());
    }

    @Test
    public void shouldRejectRunningEventWithValidationException() throws Exception {
        // Given
        Instant pastTime = Instant.now().minus(1, ChronoUnit.DAYS);

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name("Test Marathon")
                .dateTime(pastTime.toEpochMilli()) // Past date - should trigger domain validation
                .location("Test Location")
                .build();

        when(createRunningEventUseCase.createRunningEvent(any(RunningEvent.class)))
                .thenThrow(new ValidationException("Event date must be in the future"));

        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Event date must be in the future")));
    }

    @Test
    public void shouldGetRunningEventSuccessfully() throws Exception {
        // Given
        Long eventId = 1L;
        Instant eventTime = Instant.now().plus(30, ChronoUnit.DAYS);
        String formattedTime = DATE_FORMATTER.format(eventTime);

        RunningEvent event = RunningEvent.builder()
                .id(eventId)
                .name("Test Marathon")
                .dateTime(eventTime.toEpochMilli())
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        when(getRunningEventUseCase.getRunningEventById(eventId)).thenReturn(Optional.of(event));

        // When & Then
        mockMvc.perform(get("/api/events/{id}", eventId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Marathon")))
                .andExpect(jsonPath("$.dateTime", is(eventTime.toEpochMilli())))
                .andExpect(jsonPath("$.location", is("Test Location")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.furtherInformation", is("Further Information")))
                .andExpect(jsonPath("$.formattedDateTime", is(formattedTime)));
    }

    @Test
    public void shouldReturn404WhenEventNotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        when(getRunningEventUseCase.getRunningEventById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/events/{id}", nonExistentId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Running event not found with ID: 999")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void shouldHandleUnexpectedExceptions() throws Exception {
        // Given
        Long eventId = 1L;

        when(getRunningEventUseCase.getRunningEventById(eventId))
                .thenThrow(new RuntimeException("Unexpected database error"));

        // When & Then
        mockMvc.perform(get("/api/events/{id}", eventId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("An unexpected error occurred. Please try again later.")));
    }
}
