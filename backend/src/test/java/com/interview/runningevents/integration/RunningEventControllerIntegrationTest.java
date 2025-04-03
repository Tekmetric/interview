package com.interview.runningevents.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.runningevents.infrastructure.web.dto.RunningEventRequestDTO;
import com.interview.runningevents.infrastructure.web.dto.RunningEventResponseDTO;
import com.interview.runningevents.infrastructure.web.util.DateTimeConverter;

@RunningEventIntegrationTestConfig
@AutoConfigureMockMvc
public class RunningEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/events";

    // Test creating a running event
    @Test
    public void shouldCreateRunningEvent() throws Exception {
        // Given
        String futureDateTime = DateTimeConverter.fromTimestamp(
                Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name("Integration Test Event")
                .dateTime(futureDateTime)
                .location("Integration Test Location")
                .description("Integration test description")
                .furtherInformation("Integration test further info")
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test Event"))
                .andExpect(jsonPath("$.dateTime").value(futureDateTime))
                .andExpect(jsonPath("$.location").value("Integration Test Location"))
                .andReturn();

        // Extract the created event ID for further tests
        RunningEventResponseDTO createdEvent =
                objectMapper.readValue(result.getResponse().getContentAsString(), RunningEventResponseDTO.class);

        assertThat(createdEvent.getId()).isNotNull();
    }

    // Test retrieving a running event
    @Test
    public void shouldGetRunningEventById() throws Exception {
        // Given
        Long eventId = createEventAndReturnId();

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Integration Test Event"))
                .andExpect(jsonPath("$.location").value("Integration Test Location"));
    }

    // Test updating a running event
    @Test
    public void shouldUpdateRunningEvent() throws Exception {
        // Given
        Long eventId = createEventAndReturnId();
        String futureDateTime = DateTimeConverter.fromTimestamp(
                Instant.now().plus(60, ChronoUnit.DAYS).toEpochMilli());

        RunningEventRequestDTO updateDTO = RunningEventRequestDTO.builder()
                .name("Updated Event Name")
                .dateTime(futureDateTime)
                .location("Updated Location")
                .description("Updated description")
                .furtherInformation("Updated further info")
                .build();

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Updated Event Name"))
                .andExpect(jsonPath("$.location").value("Updated Location"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        // Verify the update was persisted
        mockMvc.perform(get(BASE_URL + "/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Event Name"));
    }

    // Test deleting a running event
    @Test
    public void shouldDeleteRunningEvent() throws Exception {
        // Given
        Long eventId = createEventAndReturnId();

        // When & Then
        // First verify the event exists
        mockMvc.perform(get(BASE_URL + "/{id}", eventId)).andExpect(status().isOk());

        // Delete the event
        mockMvc.perform(delete(BASE_URL + "/{id}", eventId)).andExpect(status().isNoContent());

        // Verify the event no longer exists
        mockMvc.perform(get(BASE_URL + "/{id}", eventId)).andExpect(status().isNotFound());
    }

    // Test listing running events with pagination
    @Test
    public void shouldListRunningEventsWithPagination() throws Exception {
        // Given
        createMultipleEvents(5);

        // When & Then
        mockMvc.perform(get(BASE_URL).param("page", "0").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.totalItems").value(greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.pageSize").value(3))
                .andExpect(jsonPath("$.hasNext").value(true));

        // Test second page
        mockMvc.perform(get(BASE_URL).param("page", "1").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.hasPrevious").value(true));
    }

    // Test listing running events with date filtering
    @Test
    public void shouldListRunningEventsWithDateFiltering() throws Exception {
        // Given
        Instant now = Instant.now();
        Instant nearFuture = now.plus(10, ChronoUnit.DAYS);
        Instant farFuture = now.plus(60, ChronoUnit.DAYS);

        // Create events at different times
        createEventWithDate("Near Future Event", nearFuture);
        createEventWithDate("Far Future Event", farFuture);

        String fromDateStr = DateTimeConverter.fromTimestamp(now.toEpochMilli());
        String midDateStr =
                DateTimeConverter.fromTimestamp(now.plus(30, ChronoUnit.DAYS).toEpochMilli());

        // When & Then
        // Search for events in near future only
        mockMvc.perform(get(BASE_URL).param("fromDate", fromDateStr).param("toDate", midDateStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].name", hasItem("Near Future Event")))
                .andExpect(jsonPath("$.items[*].name", not(hasItem("Far Future Event"))));
    }

    // Test listing running events with sorting
    @Test
    public void shouldListRunningEventsWithSorting() throws Exception {
        // Given - create events with unique names for this test to ensure we can find them
        String testPrefix = "SortTest-" + System.currentTimeMillis() + "-";
        Long idA = createEventWithName(testPrefix + "A-Event");
        Long idB = createEventWithName(testPrefix + "B-Event");
        Long idC = createEventWithName(testPrefix + "C-Event");

        // When & Then
        // Test ascending sort - use our unique test prefix to find just our test events
        mockMvc.perform(get(BASE_URL).param("sortBy", "name").param("sortDir", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].name", hasItem(containsString(testPrefix + "A-Event"))))
                .andExpect(jsonPath("$.items[*].name", hasItem(containsString(testPrefix + "B-Event"))))
                .andExpect(jsonPath("$.items[*].name", hasItem(containsString(testPrefix + "C-Event"))));

        // Also verify the relative ordering - ensure A comes before B, and B comes before C
        MvcResult ascResult = mockMvc.perform(
                        get(BASE_URL).param("sortBy", "name").param("sortDir", "ASC"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = ascResult.getResponse().getContentAsString();
        int indexA = responseJson.indexOf(testPrefix + "A-Event");
        int indexB = responseJson.indexOf(testPrefix + "B-Event");
        int indexC = responseJson.indexOf(testPrefix + "C-Event");

        // Verify that A comes before B, and B comes before C in the sorted response
        assertThat(indexA).isLessThan(indexB);
        assertThat(indexB).isLessThan(indexC);

        // Test descending sort
        MvcResult descResult = mockMvc.perform(
                        get(BASE_URL).param("sortBy", "name").param("sortDir", "DESC"))
                .andExpect(status().isOk())
                .andReturn();

        responseJson = descResult.getResponse().getContentAsString();
        indexA = responseJson.indexOf(testPrefix + "A-Event");
        indexB = responseJson.indexOf(testPrefix + "B-Event");
        indexC = responseJson.indexOf(testPrefix + "C-Event");

        // Verify that C comes before B, and B comes before A in the sorted response (DESC)
        assertThat(indexC).isLessThan(indexB);
        assertThat(indexB).isLessThan(indexA);
    }

    // Test validation errors
    @Test
    public void shouldReturnValidationErrorsForInvalidData() throws Exception {
        // Given
        RunningEventRequestDTO invalidRequest = RunningEventRequestDTO.builder()
                .name("") // Empty name - should fail validation
                .dateTime("") // Empty date - should fail validation
                .location("") // Empty location - should fail validation
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()").value(4));
    }

    // Test validation for past date
    @Test
    public void shouldRejectEventWithPastDate() throws Exception {
        // Given
        String pastDateTime = DateTimeConverter.fromTimestamp(
                Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli());

        RunningEventRequestDTO pastDateRequest = RunningEventRequestDTO.builder()
                .name("Past Event")
                .dateTime(pastDateTime)
                .location("Test Location")
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastDateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("future")));
    }

    // Test 404 for non-existent event
    @Test
    public void shouldReturn404ForNonExistentEvent() throws Exception {
        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    // Test 400 for invalid ID format
    @Test
    public void shouldReturn400ForInvalidIdFormat() throws Exception {
        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", "invalid-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Type mismatch")));
    }

    // Test invalid date format
    @Test
    public void shouldRejectInvalidDateFormat() throws Exception {
        // Given
        RunningEventRequestDTO invalidDateRequest = RunningEventRequestDTO.builder()
                .name("Invalid Date Event")
                .dateTime("2025/04/30 14:30") // Invalid format, should be yyyy-MM-ddTHH:mm
                .location("Test Location")
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDateRequest)))
                .andExpect(status().isBadRequest());
    }

    // Test invalid sort field
    @Test
    public void shouldRejectInvalidSortField() throws Exception {
        // When & Then
        mockMvc.perform(get(BASE_URL).param("sortBy", "invalidField"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid sort field")));
    }

    // Test invalid sort direction
    @Test
    public void shouldRejectInvalidSortDirection() throws Exception {
        // When & Then
        mockMvc.perform(get(BASE_URL).param("sortDir", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid sort direction")));
    }

    // Helper methods

    /**
     * Helper method to create an event with a specific date
     */
    private Long createEventWithDate(String name, Instant eventDate) throws Exception {
        String dateTimeStr = DateTimeConverter.fromTimestamp(eventDate.toEpochMilli());

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name(name)
                .dateTime(dateTimeStr)
                .location("Test Location for " + name)
                .build();

        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        RunningEventResponseDTO createdEvent =
                objectMapper.readValue(result.getResponse().getContentAsString(), RunningEventResponseDTO.class);

        return createdEvent.getId();
    }

    /**
     * Helper method to create an event with a specific name
     */
    private Long createEventWithName(String name) throws Exception {
        String futureDateTime = DateTimeConverter.fromTimestamp(
                Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name(name)
                .dateTime(futureDateTime)
                .location("Test Location for " + name)
                .build();

        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        RunningEventResponseDTO createdEvent =
                objectMapper.readValue(result.getResponse().getContentAsString(), RunningEventResponseDTO.class);

        return createdEvent.getId();
    }

    /**
     * Helper method to create a test event and return its ID
     */
    private Long createEventAndReturnId() throws Exception {
        String futureDateTime = DateTimeConverter.fromTimestamp(
                Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name("Integration Test Event")
                .dateTime(futureDateTime)
                .location("Integration Test Location")
                .description("Integration test description")
                .build();

        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        RunningEventResponseDTO createdEvent =
                objectMapper.readValue(result.getResponse().getContentAsString(), RunningEventResponseDTO.class);

        return createdEvent.getId();
    }

    /**
     * Helper method to create multiple test events
     */
    private void createMultipleEvents(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            String futureDateTime = DateTimeConverter.fromTimestamp(
                    Instant.now().plus(30 + i, ChronoUnit.DAYS).toEpochMilli());

            RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                    .name("Test Event " + i)
                    .dateTime(futureDateTime)
                    .location("Test Location " + i)
                    .build();

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated());
        }
    }
}
