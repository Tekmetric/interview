package com.interview.repository;

import com.interview.dto.JobPostingFilter;
import com.interview.dto.JobPostingRequest;
import com.interview.model.JobPosting;
import com.interview.model.enums.JobStatus;
import com.interview.util.JobPostingFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Slice test — loads only JPA layer (H2 + Liquibase) for fast feedback.
 */
@DataJpaTest
class JobPostingSpecificationTest {

    @Autowired
    private JobPostingRepository repository;

    @Test
    @DisplayName("isRemote(true) returns only remote postings")
    void isRemote_true_filtersCorrectly() {
        persist(JobPostingFactory.random().toBuilder().remote(true).build());
        persist(JobPostingFactory.random().toBuilder().remote(false).build());

        List<JobPosting> results = repository.findAll(
                JobPostingSpecification.isRemote(true));

        assertThat(results).isNotEmpty()
                .allMatch(JobPosting::getRemote);
    }

    @Test
    @DisplayName("isRemote(null) applies no filter")
    void isRemote_null_returnsAll() {
        persist(JobPostingFactory.random().toBuilder().remote(true).build());
        persist(JobPostingFactory.random().toBuilder().remote(false).build());

        long total = repository.count();
        List<JobPosting> results = repository.findAll(
                JobPostingSpecification.isRemote(null));

        assertThat(results).hasSize((int) total);
    }

    @Test
    @DisplayName("locationContains is case-insensitive")
    void locationContains_caseInsensitive() {
        persist(JobPostingFactory.random().toBuilder().location("Austin, TX").build());
        persist(JobPostingFactory.random().toBuilder().location("Denver, CO").build());

        List<JobPosting> results = repository.findAll(
                JobPostingSpecification.locationContains("AUSTIN"));

        assertThat(results).isNotEmpty()
                .allMatch(p -> p.getLocation().toLowerCase().contains("austin"));
    }

    @Test
    @DisplayName("locationContains(null) applies no filter")
    void locationContains_null_returnsAll() {
        persist(JobPostingFactory.random().toBuilder().location("Seattle, WA").build());

        long total = repository.count();
        List<JobPosting> results = repository.findAll(
                JobPostingSpecification.locationContains(null));

        assertThat(results).hasSize((int) total);
    }

    @Test
    @DisplayName("titleContains does partial case-insensitive match")
    void titleContains_partialMatch() {
        persist(JobPostingFactory.random().toBuilder().title("Principal Data Scientist").build());
        persist(JobPostingFactory.random().toBuilder().title("UX Researcher").build());

        List<JobPosting> results = repository.findAll(
                JobPostingSpecification.titleContains("data"));

        assertThat(results).isNotEmpty()
                .allMatch(p -> p.getTitle().toLowerCase().contains("data"));
    }

    @Test
    @DisplayName("fromFilter combining remote + titleContains")
    void fromFilter_combined_narrowsResults() {
        persist(JobPostingFactory.random().toBuilder()
                .title("Remote ML Engineer").remote(true).build());
        persist(JobPostingFactory.random().toBuilder()
                .title("Remote ML Engineer").remote(false).build());
        persist(JobPostingFactory.random().toBuilder()
                .title("On-Site Manager").remote(true).build());

        JobPostingFilter filter = JobPostingFilter.builder().remote(true).titleContains("ML Engineer").build();

        Specification<JobPosting> spec = JobPostingSpecification.fromFilter(filter);
        List<JobPosting> results = repository.findAll(spec);

        assertThat(results).isNotEmpty()
                .allMatch(p -> p.getRemote() && p.getTitle().contains("ML Engineer"));
    }

    // ── Helper ────────────────────────────────────────────────

    private JobPosting persist(JobPostingRequest req) {
        // Build entity manually — avoids pulling in the full Spring context
        JobPosting p = JobPosting.builder()
                .title(req.title())
                .company(req.company())
                .department(req.department())
                .location(req.location())
                .remote(req.remote() != null ? req.remote() : false)
                .jobType(req.jobType())
                .experienceLevel(req.experienceLevel())
                .status(req.status() != null ? req.status() : JobStatus.DRAFT)
                .salaryMin(req.salaryMin())
                .salaryMax(req.salaryMax())
                .currency(req.currency() != null ? req.currency() : "USD")
                .description(req.description())
                .requirements(req.requirements())
                .benefits(req.benefits())
                .build();
        return repository.save(p);
    }
}