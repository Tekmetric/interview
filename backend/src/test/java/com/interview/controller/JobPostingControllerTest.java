package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interview.dto.JobPostingFilter;
import com.interview.dto.JobPostingRequest;
import com.interview.dto.JobPostingResponse;
import com.interview.exception.GlobalExceptionHandler;
import com.interview.exception.IllegalStateTransitionException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.enums.ExperienceLevel;
import com.interview.model.enums.JobStatus;
import com.interview.model.enums.JobType;
import com.interview.service.JobPostingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pure web-layer unit test — no embedded server, no database.
 * <p>
 * {@link WebMvcTest} loads only:
 * - {@link JobPostingController}
 * - {@link GlobalExceptionHandler} (picked up as @RestControllerAdvice)
 * - Jackson, validation, MockMvc infrastructure
 * <p>
 * {@link JobPostingService} is replaced by a Mockito mock via {@link MockitoBean}.
 * Every test drives the controller in isolation by stubbing the service.
 */
@WebMvcTest(JobPostingController.class)
@DisplayName("JobPostingController — web-layer unit tests")
class JobPostingControllerTest {

    private static final String BASE = "/api/job-postings";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JobPostingService service;

    // ── shared fixtures ───────────────────────────────────────

    private JobPostingResponse sampleResponse;
    private JobPostingRequest validRequest;


    @BeforeEach
    void setUp() {
        sampleResponse = JobPostingResponse.builder()
                .id(1L)
                .title("Senior Backend Engineer")
                .company("Tekmetric")
                .department("Engineering")
                .location("Houston, TX")
                .remote(true)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .status(JobStatus.DRAFT)
                .salaryMin(new BigDecimal("130000.00"))
                .salaryMax(new BigDecimal("160000.00"))
                .currency("USD")
                .description("Build the platform.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        validRequest = JobPostingRequest.builder()
                .title("Senior Backend Engineer")
                .company("Tekmetric")
                .department("Engineering")
                .location("Houston, TX")
                .remote(true)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .salaryMin(new BigDecimal("130000.00"))
                .salaryMax(new BigDecimal("160000.00"))
                .currency("USD")
                .description("Build the platform.")
                .expiresAt(LocalDateTime.now().plusDays(60))
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // POST /api/job-postings — CREATE
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/job-postings")
    class Create {

        @Test
        @DisplayName("valid body → service.create called → 201 with response body")
        void create_validRequest_returns201AndDelegates() throws Exception {
            given(service.create(any(JobPostingRequest.class))).willReturn(sampleResponse);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value("Senior Backend Engineer"))
                    .andExpect(jsonPath("$.company").value("Tekmetric"))
                    .andExpect(jsonPath("$.status").value("DRAFT"))
                    .andExpect(jsonPath("$.currency").value("USD"));

            verify(service).create(any(JobPostingRequest.class));
        }

        @Test
        @DisplayName("missing title → 400, service never called")
        void create_missingTitle_returns400() throws Exception {
            JobPostingRequest bad = validRequest.toBuilder().title(null).build();

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(bad)))
                    .andExpect(status().isBadRequest());

            verifyNoMoreInteractions(service);
        }

        @Test
        @DisplayName("missing company → 400")
        void create_missingCompany_returns400() throws Exception {
            JobPostingRequest bad = validRequest.toBuilder().company(null).build();

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("missing description → 400")
        void create_missingDescription_returns400() throws Exception {
            JobPostingRequest bad = validRequest.toBuilder().description(null).build();

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("missing jobType → 400")
        void create_missingJobType_returns400() throws Exception {
            JobPostingRequest bad = validRequest.toBuilder().jobType(null).build();

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("blank body → 400")
        void create_emptyBody_returns400() throws Exception {
            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ══════════════════════════════════════════════════════════
    // GET /api/job-postings/{id} — FIND BY ID
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/job-postings/{id}")
    class FindById {

        @Test
        @DisplayName("existing id → 200 with body")
        void findById_existing_returns200() throws Exception {
            given(service.findById(1L)).willReturn(sampleResponse);

            mockMvc.perform(get(BASE + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value("Senior Backend Engineer"));
        }

        @Test
        @DisplayName("missing id → service throws ResourceNotFoundException → 404")
        void findById_missing_returns404() throws Exception {
            given(service.findById(99L))
                    .willThrow(new ResourceNotFoundException("Job posting not found with id: 99"));

            mockMvc.perform(get(BASE + "/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Resource Not Found"))
                    .andExpect(jsonPath("$.detail").value(containsString("99")));
        }
    }

    // ══════════════════════════════════════════════════════════
    // GET /api/job-postings — LIST WITH FILTERS
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/job-postings")
    class FindAll {

        @Test
        @DisplayName("no params → returns page, service called with empty filter")
        void findAll_noParams_delegatesToService() throws Exception {
            given(service.findAll(any(JobPostingFilter.class), any()))
                    .willReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            mockMvc.perform(get(BASE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("remote=true param is forwarded to service")
        void findAll_remoteParam_passedThrough() throws Exception {
            given(service.findAll(any(JobPostingFilter.class), any()))
                    .willReturn(new PageImpl<>(List.of()));

            mockMvc.perform(get(BASE).param("remote", "true"))
                    .andExpect(status().isOk());

            verify(service).findAll(any(JobPostingFilter.class), any());
        }

        @Test
        @DisplayName("titleContains + location params → 200")
        void findAll_multipleFilters_returns200() throws Exception {
            given(service.findAll(any(JobPostingFilter.class), any()))
                    .willReturn(new PageImpl<>(List.of(sampleResponse)));

            mockMvc.perform(get(BASE)
                            .param("titleContains", "engineer")
                            .param("location", "Houston"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("pagination params are accepted — size and page respected")
        void findAll_paginationParams_accepted() throws Exception {
            given(service.findAll(any(JobPostingFilter.class), any()))
                    .willReturn(new PageImpl<>(List.of()));

            mockMvc.perform(get(BASE).param("page", "2").param("size", "5"))
                    .andExpect(status().isOk());
        }
    }

    // ══════════════════════════════════════════════════════════
    // PUT /api/job-postings/{id} — UPDATE
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PUT /api/job-postings/{id}")
    class Update {

        @Test
        @DisplayName("valid body → 200, service.update called with correct id")
        void update_valid_returns200() throws Exception {
            JobPostingResponse updated = sampleResponse.toBuilder()
                    .title("Staff Engineer").build();
            given(service.update(eq(1L), any(JobPostingRequest.class))).willReturn(updated);

            mockMvc.perform(put(BASE + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Staff Engineer"));

            verify(service).update(eq(1L), any(JobPostingRequest.class));
        }

        @Test
        @DisplayName("not found → 404")
        void update_notFound_returns404() throws Exception {
            given(service.update(eq(99L), any()))
                    .willThrow(new ResourceNotFoundException("Job posting not found with id: 99"));

            mockMvc.perform(put(BASE + "/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(validRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("illegal state (CLOSED) → 409 with RFC-7807 body")
        void update_illegalState_returns409() throws Exception {
            given(service.update(eq(1L), any()))
                    .willThrow(new IllegalStateTransitionException("Cannot edit a CLOSED job posting."));

            mockMvc.perform(put(BASE + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(validRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Illegal State Transition"))
                    .andExpect(jsonPath("$.detail").value(containsString("CLOSED")));
        }

        @Test
        @DisplayName("invalid body → 400, service never called")
        void update_invalidBody_returns400() throws Exception {
            JobPostingRequest bad = validRequest.toBuilder().title("").build();

            mockMvc.perform(put(BASE + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(bad)))
                    .andExpect(status().isBadRequest());

            verifyNoMoreInteractions(service);
        }
    }


    // ══════════════════════════════════════════════════════════
    // DELETE /api/job-postings/{id}
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("DELETE /api/job-postings/{id}")
    class Delete {

        @Test
        @DisplayName("existing id → service.delete called → 204 no content")
        void delete_existing_returns204() throws Exception {
            mockMvc.perform(delete(BASE + "/1"))
                    .andExpect(status().isNoContent());

            verify(service).delete(1L);
        }

        @Test
        @DisplayName("missing id → 404")
        void delete_notFound_returns404() throws Exception {
            willThrow(new ResourceNotFoundException("Job posting not found with id: 99"))
                    .given(service).delete(99L);

            mockMvc.perform(delete(BASE + "/99"))
                    .andExpect(status().isNotFound());
        }
    }

    // ── helpers ───────────────────────────────────────────────

    private String toJson(Object obj) throws Exception {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()).writeValueAsString(obj);
    }
}