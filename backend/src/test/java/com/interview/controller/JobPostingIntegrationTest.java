package com.interview.controller;

import com.interview.dto.JobPostingRequest;
import com.interview.dto.JobPostingResponse;
import com.interview.model.enums.ExperienceLevel;
import com.interview.model.enums.JobType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * End-to-end integration test using Spring Boot 4 RestClient.
 * <p>
 * Uses the modern RestClient to make actual HTTP calls to the embedded server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Job Posting API — end-to-end integration tests")
class JobPostingIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api/job-postings")
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // CRUD BASICS
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Basic CRUD")
    class BasicCrud {

        @Test
        @DisplayName("create → retrieve by id → data matches")
        void createAndFetch_dataMatches() {
            JobPostingRequest req = buildRequest("Platform Engineer");

            JobPostingResponse created = restClient.post()
                    .body(req)
                    .retrieve()
                    .body(JobPostingResponse.class);

            assertThat(created.id()).isPositive();

            JobPostingResponse fetched = restClient.get()
                    .uri("/{id}", created.id())
                    .retrieve()
                    .body(JobPostingResponse.class);

            assertThat(fetched.title()).isEqualTo("Platform Engineer");
            assertThat(fetched.company()).isEqualTo("Tekmetric");
            assertThat(fetched.jobType()).isEqualTo(JobType.FULL_TIME);
            assertThat(fetched.experienceLevel()).isEqualTo(ExperienceLevel.SENIOR);
            assertThat(fetched.currency()).isEqualTo("USD");
            assertThat(fetched.createdAt()).isNotNull();
            assertThat(fetched.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("create → update → updated fields reflected")
        void createAndUpdate_changedFieldsReflected() {
            JobPostingResponse created = restClient.post()
                    .body(buildRequest("Original Title"))
                    .retrieve()
                    .body(JobPostingResponse.class);

            long id = created.id();

            JobPostingRequest updatedReq = buildRequest("Updated Title").toBuilder()
                    .salaryMin(new BigDecimal("200000"))
                    .salaryMax(new BigDecimal("250000"))
                    .build();

            ResponseEntity<JobPostingResponse> updated = restClient.put()
                    .uri("/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updatedReq)
                    .retrieve()
                    .toEntity(JobPostingResponse.class);

            assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updated.getBody().title()).isEqualTo("Updated Title");
            assertThat(updated.getBody().salaryMin()).isEqualByComparingTo("200000");
        }

        @Test
        @DisplayName("create → delete → 404 on subsequent fetch")
        void createDeleteFetch_returns404() {
            JobPostingResponse created = restClient.post()
                    .body(buildRequest("To Be Deleted"))
                    .retrieve()
                    .body(JobPostingResponse.class);

            long id = created.id();

            restClient.delete().uri("/{id}", id).retrieve().toBodilessEntity();

            assertThatThrownBy(() -> {
                restClient.get().uri("/{id}", id).retrieve().toBodilessEntity();
            }).isInstanceOf(HttpClientErrorException.NotFound.class);
        }
    }

    // ══════════════════════════════════════════════════════════
    // ERROR RESPONSES (RFC 7807)
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Error response shapes")
    class ErrorShapes {

        @Test
        @DisplayName("GET unknown id → 404 with RFC-7807 fields")
        void getUnknownId_404_problemShape() {
            HttpClientErrorException.NotFound ex = org.junit.jupiter.api.Assertions.assertThrows(
                    HttpClientErrorException.NotFound.class,
                    () -> restClient.get().uri("/999999").retrieve().toBodilessEntity()
            );

            ProblemDetail problem = ex.getResponseBodyAs(ProblemDetail.class);

            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(problem).isNotNull()
                    .satisfies(p -> {
                        assertThat(p.getTitle()).isEqualTo("Resource Not Found");
                        assertThat(p.getStatus()).isEqualTo(404);
                        assertThat(p.getDetail()).contains("999999");
                    });
        }

        @Test
        @DisplayName("publish non-existent id → 404 with problem detail")
        void publishMissing_404() {
            HttpClientErrorException.NotFound ex = org.junit.jupiter.api.Assertions.assertThrows(
                    HttpClientErrorException.NotFound.class,
                    () -> restClient.post().uri("/999999/publish").retrieve().toBodilessEntity()
            );

            Map<String, Object> body = ex.getResponseBodyAs(new ParameterizedTypeReference<Map<String, Object>>() {});
            assertThat(body.get("status")).isEqualTo(404);
        }

        @Test
        @DisplayName("delete non-existent id → 404")
        void deleteMissing_404() {
            assertThatThrownBy(() -> {
                restClient.delete().uri("/999999").retrieve().toBodilessEntity();
            }).isInstanceOf(HttpClientErrorException.NotFound.class);
        }
    }

    // ══════════════════════════════════════════════════════════
    // FILTERING
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Filtering via query params")
    class Filtering {

        @Test
        @DisplayName("remote=true returns only remote postings")
        void filterRemoteTrue_onlyRemotePostings() {
            restClient.post().body(buildRequest("Remote Role").toBuilder().remote(true).build()).retrieve().toBodilessEntity();
            restClient.post().body(buildRequest("On-Site Role").toBuilder().remote(false).build()).retrieve().toBodilessEntity();

            PageResponse<JobPostingResponse> page = restClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("remote", "true").build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageResponse<JobPostingResponse>>() {});

            assertThat(page.content()).allSatisfy(p -> {
                assertThat(p.remote()).isTrue();
            });

            assertThat(page.content()).allMatch(JobPostingResponse::remote);
        }

        @Test
        @DisplayName("titleContains=unique matches only that posting")
        void filterTitleContains_matchesCorrectPosting() {
            String uniqueTitle = "XYZUNIQUE987SpecialEngineer";
            restClient.post().body(buildRequest(uniqueTitle)).retrieve().toBodilessEntity();

            PageResponse<JobPostingResponse> page = restClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("titleContains", "XYZUNIQUE987").build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageResponse<JobPostingResponse>>() {});

            assertThat(page.content()).anyMatch(p -> p.title().contains("XYZUNIQUE987"));
        }

        @Test
        @DisplayName("location filter is case-insensitive")
        void filterLocation_caseInsensitive() {
            JobPostingRequest req = buildRequest("Location Test Role").toBuilder().location("Seattle, WA").build();
            restClient.post().body(req).retrieve().toBodilessEntity();

            PageResponse<JobPostingResponse> page = restClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("location", "seattle").build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageResponse<JobPostingResponse>>() {});

            assertThat(page.content()).anyMatch(p -> p.location() != null && p.location().toLowerCase().contains("seattle"));
        }

        @Test
        @DisplayName("titleContains with no match returns empty content array")
        void filterTitleContains_noMatch_emptyContent() {
            PageResponse<JobPostingResponse> page = restClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("titleContains", "zzznomatch999abc").build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageResponse<JobPostingResponse>>() {});

            assertThat(page.content()).isEmpty();
        }

        @Test
        @DisplayName("page size is respected in response metadata")
        void pagination_sizeIsRespected() {
            PageResponse<JobPostingResponse> page = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("page", "0")
                            .queryParam("size", "3")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageResponse<JobPostingResponse>>() {});

            assertThat(page.content()).hasSizeLessThanOrEqualTo(3);
            assertThat(page.totalElements()).isGreaterThan(3);
        }
    }

    // ══════════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════════

    private JobPostingRequest buildRequest(String title) {
        return JobPostingRequest.builder()
                .title(title)
                .company("Tekmetric")
                .department("Engineering")
                .location("Houston, TX")
                .remote(false)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .salaryMin(new BigDecimal("130000"))
                .salaryMax(new BigDecimal("160000"))
                .currency("USD")
                .description("Full job description for " + title)
                .requirements("5+ years experience")
                .benefits("Full benefits package")
                .expiresAt(LocalDateTime.now().plusDays(90))
                .build();
    }

    record PageResponse<T>(java.util.List<T> content, long totalElements, int totalPages, int size) {}
}