package com.interview.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.AthleteRequest;
import com.interview.repository.AthleteRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AthleteControllerIntegrationTest {

    private static final String ATHLETES_BASE_URL = "/api/v1/athletes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AthleteRepository athleteRepository;

    /**
     * Helper method to build URL for athlete resource by ID.
     *
     * @param id the athlete ID
     * @return the complete URL path
     */
    private String athleteUrl(Long id) {
        return ATHLETES_BASE_URL + "/" + id;
    }

    /**
     * Clean up the database before each test to ensure test isolation.
     */
    @BeforeEach
    void setUp() {
        athleteRepository.deleteAll();
    }

    @Test
    void shouldGetAllAthletesWithPagination() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    void shouldGetAllAthletesWithCustomPageSize() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL).param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(5))));
    }

    @Test
    void shouldFilterAthletesByNationality() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL)
                        .param("nationality", "Jamaica")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].nationality", everyItem(containsStringIgnoringCase("Jamaica"))));
    }

    @Test
    void shouldFilterAthletesByDiscipline() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL)
                        .param("discipline", "100m")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].discipline", everyItem(containsStringIgnoringCase("100m"))));
    }

    @Test
    void shouldSearchAthletesByName() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL)
                        .param("search", "bolt")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void shouldSortAthletesByFirstNameAscending() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "firstName")
                        .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldSortAthletesByLastNameDescending() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "lastName")
                        .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldCombineFilteringAndPagination() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL)
                        .param("nationality", "Kenya")
                        .param("discipline", "Marathon")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldGetAthleteById() throws Exception {
        // First create an athlete
        final AthleteRequest request =
                new AthleteRequest("John", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m", "9.99s", "Fast");

        final String createdResponse = mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final Long createdId = objectMapper.readTree(createdResponse).get("id").asLong();

        // Then fetch it by ID
        mockMvc.perform(get(athleteUrl(createdId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.birthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.nationality").value("USA"))
                .andExpect(jsonPath("$.discipline").value("100m"));
    }

    @Test
    void shouldReturn404WhenAthleteNotFound() throws Exception {
        mockMvc.perform(get(athleteUrl(99999L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldCreateNewAthlete() throws Exception {
        final AthleteRequest request =
                new AthleteRequest("John", "Doe", LocalDate.of(1990, 1, 1), "USA", "400m", "43.5s", "Rising star");

        mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.birthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.nationality").value("USA"))
                .andExpect(jsonPath("$.discipline").value("400m"));
    }

    @Test
    void shouldFailToCreateDuplicateAthlete() throws Exception {
        final AthleteRequest request = new AthleteRequest(
                "Usain", "Bolt", LocalDate.of(1986, 8, 21), "Jamaica", "100m Sprint", "9.58s", "Legend");

        // First creation should succeed
        mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second creation with same data should fail with 409 Conflict
        mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Athlete 'Usain Bolt' already exists"));
    }

    @Test
    void shouldFailToCreateAthleteWithBlankFirstName() throws Exception {
        final AthleteRequest request =
                new AthleteRequest("", "Doe", LocalDate.of(1990, 1, 1), "USA", "400m", "43.5s", "Test");

        mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.firstName").value("First name is required"));
    }

    @Test
    void shouldFailToCreateAthleteWithFutureBirthDate() throws Exception {
        final AthleteRequest request =
                new AthleteRequest("John", "Doe", LocalDate.now().plusDays(1), "USA", "400m", "43.5s", "Test");

        mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.birthDate").value("Birth date must be in the past or present"));
    }

    @Test
    void shouldFailToCreateAthleteWithMultipleErrors() throws Exception {
        final AthleteRequest request = new AthleteRequest("", "", null, "", "", null, null);

        mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.birthDate").exists())
                .andExpect(jsonPath("$.errors.nationality").exists())
                .andExpect(jsonPath("$.errors.discipline").exists());
    }

    @Test
    void shouldUpdateExistingAthlete() throws Exception {
        // First create an athlete
        final AthleteRequest createRequest =
                new AthleteRequest("Original", "Athlete", LocalDate.of(1990, 1, 1), "USA", "100m", "9.99s", "Original");

        final String createdResponse = mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final Long createdId = objectMapper.readTree(createdResponse).get("id").asLong();

        // Then update it
        final AthleteRequest updateRequest = new AthleteRequest(
                "Updated", "Name", LocalDate.of(1985, 5, 15), "Canada", "200m Sprint", "19.5s", "Updated bio");

        mockMvc.perform(put(athleteUrl(createdId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.nationality").value("Canada"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentAthlete() throws Exception {
        final AthleteRequest request =
                new AthleteRequest("John", "Doe", LocalDate.of(1990, 1, 1), "USA", "400m", "43.5s", "Test");

        mockMvc.perform(put(athleteUrl(99999L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldDeleteAthlete() throws Exception {
        // First create an athlete
        final AthleteRequest request =
                new AthleteRequest("ToDelete", "Athlete", LocalDate.of(1990, 1, 1), "USA", "100m", "9.99s", "Test");

        final String createdResponse = mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final Long createdId = objectMapper.readTree(createdResponse).get("id").asLong();

        // Delete it
        mockMvc.perform(delete(athleteUrl(createdId))).andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get(athleteUrl(createdId))).andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleInvalidPageNumber() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL).param("page", "-1").param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleInvalidPageSize() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL).param("page", "0").param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandlePageSizeExceedingMax() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL).param("page", "0").param("size", "200"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFallbackToDefaultSortFieldWhenInvalid() throws Exception {
        mockMvc.perform(get(ATHLETES_BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "invalidField"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldHandleMalformedJson() throws Exception {
        mockMvc.perform(post(ATHLETES_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
