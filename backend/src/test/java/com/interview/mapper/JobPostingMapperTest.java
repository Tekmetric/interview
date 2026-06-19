package com.interview.mapper;

import com.interview.dto.JobPostingRequest;
import com.interview.dto.JobPostingResponse;
import com.interview.model.JobPosting;
import com.interview.model.enums.ExperienceLevel;
import com.interview.model.enums.JobStatus;
import com.interview.model.enums.JobType;
import com.interview.util.JobPostingFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JobPostingMapperTest {

    private final JobPostingMapper mapper = Mappers.getMapper(JobPostingMapper.class);

    @Test
    @DisplayName("toEntity maps all user-supplied fields correctly")
    void toEntity_mapsFields() {
        JobPostingRequest req = JobPostingFactory.withSeed(1L);

        JobPosting entity = mapper.toEntity(req);

        assertThat(entity.getTitle()).isEqualTo(req.title());
        assertThat(entity.getCompany()).isEqualTo(req.company());
        assertThat(entity.getDepartment()).isEqualTo(req.department());
        assertThat(entity.getLocation()).isEqualTo(req.location());
        assertThat(entity.getRemote()).isEqualTo(req.remote());
        assertThat(entity.getJobType()).isEqualTo(req.jobType());
        assertThat(entity.getExperienceLevel()).isEqualTo(req.experienceLevel());
        assertThat(entity.getStatus()).isEqualTo(req.status());
        assertThat(entity.getSalaryMin()).isEqualByComparingTo(req.salaryMin());
        assertThat(entity.getSalaryMax()).isEqualByComparingTo(req.salaryMax());
        assertThat(entity.getCurrency()).isEqualTo(req.currency());
        assertThat(entity.getDescription()).isEqualTo(req.description());
        assertThat(entity.getRequirements()).isEqualTo(req.requirements());
        assertThat(entity.getBenefits()).isEqualTo(req.benefits());
        assertThat(entity.getExpiresAt()).isEqualTo(req.expiresAt());
    }

    @Test
    @DisplayName("toEntity: null status defaults to DRAFT")
    void toEntity_nullStatus_defaultsDraft() {
        JobPostingRequest req = JobPostingFactory.random().toBuilder().status(null).build();
        assertThat(mapper.toEntity(req).getStatus()).isEqualTo(JobStatus.DRAFT);
    }

    @Test
    @DisplayName("toEntity: null remote defaults to false")
    void toEntity_nullRemote_defaultsFalse() {
        JobPostingRequest req = JobPostingFactory.random().toBuilder().remote(null).build();
        assertThat(mapper.toEntity(req).getRemote()).isFalse();
    }

    @Test
    @DisplayName("toEntity: null currency defaults to USD")
    void toEntity_nullCurrency_defaultsUSD() {
        JobPostingRequest req = JobPostingFactory.random().toBuilder().currency(null).build();
        assertThat(mapper.toEntity(req).getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("toEntity: server-managed fields are not set")
    void toEntity_serverFieldsIgnored() {
        JobPosting entity = mapper.toEntity(JobPostingFactory.random());

        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("toEntity: status ACTIVE triggers postedAt stamp")
    void toEntity_activeStatus_stampsPostedAt() {
        JobPostingRequest req = JobPostingFactory.random().toBuilder()
                .status(JobStatus.ACTIVE).build();

        JobPosting entity = mapper.toEntity(req);

        assertThat(entity.getPostedAt()).isNotNull();
    }

    @Test
    @DisplayName("updateEntity updates mutable fields, leaves id/timestamps")
    void updateEntity_onlyUpdatesMutableFields() {
        JobPosting existing = JobPosting.builder()
                .id(99L)
                .title("Old Title")
                .company("Old Corp")
                .description("Old desc")
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.MID)
                .status(JobStatus.DRAFT)
                .remote(false)
                .currency("USD")
                .build();

        LocalDateTime originalCreatedAt = LocalDateTime.of(2024, 1, 1, 0, 0);
        existing.setCreatedAt(originalCreatedAt);

        JobPostingRequest update = JobPostingFactory.random().toBuilder()
                .title("New Title").build();

        mapper.updateEntity(update, existing);

        assertThat(existing.getId()).isEqualTo(99L);
        assertThat(existing.getTitle()).isEqualTo("New Title");
        // createdAt is ignored by the mapper — @PreUpdate would update updatedAt
        assertThat(existing.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("toResponse maps all entity fields to response DTO")
    void toResponse_mapsAllFields() {
        JobPosting entity = JobPosting.builder()
                .id(1L)
                .title("Test Engineer")
                .company("Acme")
                .jobType(JobType.CONTRACT)
                .experienceLevel(ExperienceLevel.SENIOR)
                .status(JobStatus.ACTIVE)
                .remote(true)
                .currency("EUR")
                .description("Do the thing")
                .salaryMin(BigDecimal.valueOf(90_000))
                .salaryMax(BigDecimal.valueOf(120_000))
                .build();

        JobPostingResponse response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Test Engineer");
        assertThat(response.company()).isEqualTo("Acme");
        assertThat(response.jobType()).isEqualTo(JobType.CONTRACT);
        assertThat(response.experienceLevel()).isEqualTo(ExperienceLevel.SENIOR);
        assertThat(response.status()).isEqualTo(JobStatus.ACTIVE);
        assertThat(response.remote()).isTrue();
        assertThat(response.currency()).isEqualTo("EUR");
        assertThat(response.description()).isEqualTo("Do the thing");
        assertThat(response.salaryMin()).isEqualByComparingTo(BigDecimal.valueOf(90_000));
        assertThat(response.salaryMax()).isEqualByComparingTo(BigDecimal.valueOf(120_000));
    }
}
