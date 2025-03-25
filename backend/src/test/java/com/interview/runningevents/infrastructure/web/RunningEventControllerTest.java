package com.interview.runningevents.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.model.SortDirection;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.DeleteRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.application.port.in.ListRunningEventsUseCase;
import com.interview.runningevents.application.port.in.UpdateRunningEventUseCase;
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

    @MockBean
    private ListRunningEventsUseCase listRunningEventsUseCase;

    @MockBean
    private UpdateRunningEventUseCase updateRunningEventUseCase;

    @MockBean
    private DeleteRunningEventUseCase deleteRunningEventUseCase;

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

    @Test
    public void shouldListRunningEventsSuccessfully() throws Exception {
        // Given
        Instant now = Instant.now();
        Instant future1 = now.plus(10, ChronoUnit.DAYS);
        Instant future2 = now.plus(20, ChronoUnit.DAYS);

        RunningEvent event1 = RunningEvent.builder()
                .id(1L)
                .name("Event 1")
                .dateTime(future1.toEpochMilli())
                .location("Location 1")
                .build();

        RunningEvent event2 = RunningEvent.builder()
                .id(2L)
                .name("Event 2")
                .dateTime(future2.toEpochMilli())
                .location("Location 2")
                .build();

        List<RunningEvent> events = Arrays.asList(event1, event2);
        PaginatedResult<RunningEvent> paginatedResult = PaginatedResult.of(events, 10, 0, 5);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(paginatedResult);

        // When & Then
        mockMvc.perform(get("/api/events").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", notNullValue()))
                .andExpect(jsonPath("$.items.length()", is(2)))
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Event 1")))
                .andExpect(jsonPath("$.items[1].id", is(2)))
                .andExpect(jsonPath("$.items[1].name", is("Event 2")))
                .andExpect(jsonPath("$.totalItems", is(10)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.pageSize", is(5)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.hasPrevious", is(false)))
                .andExpect(jsonPath("$.hasNext", is(true)));
    }

    @Test
    public void shouldApplyPaginationParameters() throws Exception {
        // Given
        RunningEvent event = RunningEvent.builder()
                .id(5L)
                .name("Event 5")
                .dateTime(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli())
                .location("Location 5")
                .build();

        List<RunningEvent> events = Arrays.asList(event);
        PaginatedResult<RunningEvent> paginatedResult = PaginatedResult.of(events, 20, 2, 10);

        // Capture the query to verify parameters are correctly passed
        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);
        when(listRunningEventsUseCase.listRunningEvents(queryCaptor.capture())).thenReturn(paginatedResult);

        // When & Then
        mockMvc.perform(get("/api/events")
                        .param("page", "2")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(1)))
                .andExpect(jsonPath("$.items[0].id", is(5)))
                .andExpect(jsonPath("$.page", is(2)))
                .andExpect(jsonPath("$.pageSize", is(10)));

        // Verify the pagination parameters were correctly passed to the use case
        RunningEventQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getPage()).isEqualTo(2);
        assertThat(capturedQuery.getPageSize()).isEqualTo(10);
    }

    @Test
    public void shouldApplyDateFilters() throws Exception {
        // Given
        RunningEvent event = RunningEvent.builder()
                .id(1L)
                .name("Event in Range")
                .dateTime(Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli())
                .location("Location 1")
                .build();

        List<RunningEvent> events = Arrays.asList(event);
        PaginatedResult<RunningEvent> paginatedResult = PaginatedResult.of(events, 1, 0, 20);

        // Capture the query to verify filter parameters
        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);
        when(listRunningEventsUseCase.listRunningEvents(queryCaptor.capture())).thenReturn(paginatedResult);

        // Define filter date range
        Long startDate = Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli();
        Long endDate = Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli();

        // When & Then
        mockMvc.perform(get("/api/events")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Event in Range")))
                .andExpect(jsonPath("$.totalItems", is(1)));

        // Verify the date filter parameters were correctly passed to the use case
        RunningEventQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getFromDate()).isEqualTo(startDate);
        assertThat(capturedQuery.getToDate()).isEqualTo(endDate);
    }

    @Test
    public void shouldHandleEmptyResults() throws Exception {
        // Given
        List<RunningEvent> emptyList = Collections.emptyList();
        PaginatedResult<RunningEvent> emptyResult = PaginatedResult.of(emptyList, 0, 0, 20);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        // When & Then
        mockMvc.perform(get("/api/events").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", notNullValue()))
                .andExpect(jsonPath("$.items.length()", is(0)))
                .andExpect(jsonPath("$.totalItems", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)))
                .andExpect(jsonPath("$.hasPrevious", is(false)))
                .andExpect(jsonPath("$.hasNext", is(false)));
    }

    @Test
    public void shouldApplySortingParameters() throws Exception {
        // Given
        List<RunningEvent> events = Arrays.asList(
                RunningEvent.builder().id(1L).name("Event A").build(),
                RunningEvent.builder().id(2L).name("Event B").build());
        PaginatedResult<RunningEvent> result = PaginatedResult.of(events, 2, 0, 10);

        // Capture the query to verify sort parameters
        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);
        when(listRunningEventsUseCase.listRunningEvents(queryCaptor.capture())).thenReturn(result);

        // When & Then
        mockMvc.perform(get("/api/events")
                        .param("sortBy", "name")
                        .param("sortDir", "DESC")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(2)));

        // Verify the sort parameters were correctly passed to the use case
        RunningEventQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getSortBy()).isEqualTo("name");
        assertThat(capturedQuery.getSortDirection()).isEqualTo(SortDirection.DESC);
    }

    @Test
    public void shouldUpdateRunningEventSuccessfully() throws Exception {
        // Given
        Long eventId = 1L;
        Instant eventTime = Instant.now().plus(30, ChronoUnit.DAYS);
        String formattedTime = DATE_FORMATTER.format(eventTime);

        RunningEvent existingEvent = RunningEvent.builder()
                .id(eventId)
                .name("Original Name")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Original Location")
                .description("Original Description")
                .build();

        RunningEventRequestDTO updateRequestDTO = RunningEventRequestDTO.builder()
                .name("Updated Name")
                .dateTime(eventTime.toEpochMilli())
                .location("Updated Location")
                .description("Updated Description")
                .furtherInformation("Updated Further Information")
                .build();

        RunningEvent updatedEvent = RunningEvent.builder()
                .id(eventId)
                .name("Updated Name")
                .dateTime(eventTime.toEpochMilli())
                .location("Updated Location")
                .description("Updated Description")
                .furtherInformation("Updated Further Information")
                .build();

        // Mock the dependencies
        when(getRunningEventUseCase.getRunningEventById(eventId)).thenReturn(Optional.of(existingEvent));

        when(updateRunningEventUseCase.updateRunningEvent(any(RunningEvent.class)))
                .thenReturn(Optional.of(updatedEvent));

        // When & Then
        mockMvc.perform(put("/api/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.dateTime", is(eventTime.toEpochMilli())))
                .andExpect(jsonPath("$.location", is("Updated Location")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.furtherInformation", is("Updated Further Information")))
                .andExpect(jsonPath("$.formattedDateTime", is(formattedTime)));
    }

    @Test
    public void shouldReturn404WhenUpdatingNonExistentEvent() throws Exception {
        // Given
        Long nonExistentId = 999L;
        Instant eventTime = Instant.now().plus(30, ChronoUnit.DAYS);

        RunningEventRequestDTO updateRequestDTO = RunningEventRequestDTO.builder()
                .name("Updated Name")
                .dateTime(eventTime.toEpochMilli())
                .location("Updated Location")
                .description("Updated Description")
                .build();

        // Mock the dependencies
        when(getRunningEventUseCase.getRunningEventById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/events/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Running event not found with ID: 999")));
    }

    @Test
    public void shouldRejectUpdateWithInvalidData() throws Exception {
        // Given
        Long eventId = 1L;

        RunningEvent existingEvent = RunningEvent.builder()
                .id(eventId)
                .name("Original Name")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Original Location")
                .build();

        RunningEventRequestDTO invalidDTO = RunningEventRequestDTO.builder()
                .name("") // Empty name - should fail validation
                .dateTime(null) // Null dateTime - should fail validation
                .location("Updated Location")
                .build();

        // Mock the dependencies
        when(getRunningEventUseCase.getRunningEventById(eventId)).thenReturn(Optional.of(existingEvent));

        // When & Then
        mockMvc.perform(put("/api/events/{id}", eventId)
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
    public void shouldRejectUpdateWithValidationException() throws Exception {
        // Given
        Long eventId = 1L;
        Instant pastTime = Instant.now().minus(1, ChronoUnit.DAYS);

        RunningEvent existingEvent = RunningEvent.builder()
                .id(eventId)
                .name("Original Name")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Original Location")
                .build();

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name("Updated Name")
                .dateTime(pastTime.toEpochMilli()) // Past date - should trigger domain validation
                .location("Updated Location")
                .build();

        // Mock the dependencies
        when(getRunningEventUseCase.getRunningEventById(eventId)).thenReturn(Optional.of(existingEvent));

        when(updateRunningEventUseCase.updateRunningEvent(any(RunningEvent.class)))
                .thenThrow(new ValidationException("Event date must be in the future"));

        // When & Then
        mockMvc.perform(put("/api/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Event date must be in the future")));
    }

    @Test
    public void shouldVerifyCorrectParametersPassedToUpdateUseCase() throws Exception {
        // Given
        Long eventId = 1L;
        Instant eventTime = Instant.now().plus(30, ChronoUnit.DAYS);

        RunningEvent existingEvent = RunningEvent.builder()
                .id(eventId)
                .name("Original Name")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Original Location")
                .build();

        RunningEventRequestDTO updateRequestDTO = RunningEventRequestDTO.builder()
                .name("Updated Name")
                .dateTime(eventTime.toEpochMilli())
                .location("Updated Location")
                .description("Updated Description")
                .build();

        RunningEvent updatedEvent = RunningEvent.builder()
                .id(eventId)
                .name("Updated Name")
                .dateTime(eventTime.toEpochMilli())
                .location("Updated Location")
                .description("Updated Description")
                .build();

        // Capture the domain event passed to the use case
        ArgumentCaptor<RunningEvent> eventCaptor = ArgumentCaptor.forClass(RunningEvent.class);

        // Mock the dependencies
        when(getRunningEventUseCase.getRunningEventById(eventId)).thenReturn(Optional.of(existingEvent));

        when(updateRunningEventUseCase.updateRunningEvent(eventCaptor.capture()))
                .thenReturn(Optional.of(updatedEvent));

        // When
        mockMvc.perform(put("/api/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk());

        // Then
        RunningEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getId()).isEqualTo(eventId);
        assertThat(capturedEvent.getName()).isEqualTo("Updated Name");
        assertThat(capturedEvent.getDateTime()).isEqualTo(eventTime.toEpochMilli());
        assertThat(capturedEvent.getLocation()).isEqualTo("Updated Location");
        assertThat(capturedEvent.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void shouldDeleteRunningEventSuccessfully() throws Exception {
        // Given
        Long eventId = 1L;

        when(deleteRunningEventUseCase.deleteRunningEvent(eventId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/events/{id}", eventId)).andExpect(status().isNoContent());

        // Verify the correct ID was passed to the use case
        verify(deleteRunningEventUseCase).deleteRunningEvent(eq(eventId));
    }

    @Test
    public void shouldReturn404WhenDeletingNonExistentEvent() throws Exception {
        // Given
        Long nonExistentId = 999L;

        when(deleteRunningEventUseCase.deleteRunningEvent(nonExistentId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/events/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Running event not found with ID: 999")));

        // Verify the correct ID was passed to the use case
        verify(deleteRunningEventUseCase).deleteRunningEvent(eq(nonExistentId));
    }
}
