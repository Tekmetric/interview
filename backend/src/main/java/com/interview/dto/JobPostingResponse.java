package com.interview.dto;


import com.interview.model.enums.ExperienceLevel;
import com.interview.model.enums.JobStatus;
import com.interview.model.enums.JobType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Schema(description = "A job posting returned by the API")
public record JobPostingResponse(
        @Schema(description = "Unique identifier", example = "1")
        Long id,

        @Schema(description = "Job title", example = "Senior Backend Engineer")
        String title,

        @Schema(description = "Hiring company", example = "Tekmetric")
        String company,

        @Schema(description = "Department", example = "Engineering")
        String department,

        @Schema(description = "Office location", example = "Houston, TX")
        String location,

        @Schema(description = "Whether fully remote", example = "true")
        Boolean remote,

        @Schema(description = "Employment type", example = "FULL_TIME")
        JobType jobType,

        @Schema(description = "Seniority level", example = "SENIOR")
        ExperienceLevel experienceLevel,

        @Schema(description = "Publication status", example = "ACTIVE")
        JobStatus status,

        @Schema(description = "Minimum salary", example = "130000.00")
        BigDecimal salaryMin,

        @Schema(description = "Maximum salary", example = "160000.00")
        BigDecimal salaryMax,

        @Schema(description = "Currency code", example = "USD")
        String currency,

        @Schema(description = "Full job description")
        String description,

        @Schema(description = "Requirements text")
        String requirements,

        @Schema(description = "Benefits text")
        String benefits,

        @Schema(description = "When the posting was published")
        LocalDateTime postedAt,

        @Schema(description = "When the posting expires")
        LocalDateTime expiresAt,

        @Schema(description = "Record creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Record last-update timestamp")
        LocalDateTime updatedAt
) {
}
