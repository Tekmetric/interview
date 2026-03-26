package com.interview.dto;

import com.interview.model.enums.ExperienceLevel;
import com.interview.model.enums.JobStatus;
import com.interview.model.enums.JobType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Schema(description = "Payload for creating or updating a job posting")
public record JobPostingRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title must not exceed 120 characters")
        @Schema(description = "Job title", example = "Senior Backend Engineer")
        String title,

        @NotBlank(message = "Company is required")
        @Size(max = 100, message = "Company name must not exceed 100 characters")
        @Schema(description = "Hiring company", example = "Tekmetric")
        String company,

        @Size(max = 100, message = "Department must not exceed 100 characters")
        @Schema(description = "Department within the company", example = "Engineering")
        String department,

        @Size(max = 120, message = "Location must not exceed 120 characters")
        @Schema(description = "Office location (city, state or country)", example = "Houston, TX")
        String location,

        @Schema(description = "Whether the role is fully remote", example = "true", defaultValue = "false")
        Boolean remote,

        @NotNull(message = "Job type is required")
        @Schema(description = "Employment type", example = "FULL_TIME",
                allowableValues = {"FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP"})
        JobType jobType,

        @NotNull(message = "Experience level is required")
        @Schema(description = "Seniority level", example = "SENIOR",
                allowableValues = {"JUNIOR", "MID", "SENIOR", "LEAD", "EXECUTIVE"})
        ExperienceLevel experienceLevel,

        @Schema(description = "Publication status — defaults to DRAFT when omitted", example = "ACTIVE",
                allowableValues = {"DRAFT", "ACTIVE", "CLOSED", "ARCHIVED"})
        JobStatus status,

        @DecimalMin(value = "0.0", message = "Minimum salary must be non-negative")
        @Digits(integer = 10, fraction = 2, message = "Invalid salary format")
        @Schema(description = "Minimum salary / hourly rate", example = "130000.00")
        BigDecimal salaryMin,

        @DecimalMin(value = "0.0", message = "Maximum salary must be non-negative")
        @Digits(integer = 10, fraction = 2, message = "Invalid salary format")
        @Schema(description = "Maximum salary / hourly rate", example = "160000.00")
        BigDecimal salaryMax,

        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        @Schema(description = "ISO 4217 currency code", example = "USD", defaultValue = "USD")
        String currency,

        @NotBlank(message = "Description is required")
        @Schema(description = "Full job description", example = "Join our platform team...")
        String description,

        @Schema(description = "List of skills and experience required")
        String requirements,

        @Schema(description = "Perks and benefits offered")
        String benefits,

        @Future(message = "Expiry date must be in the future")
        @Schema(description = "Date after which the posting expires", example = "2026-12-31T00:00:00")
        LocalDateTime expiresAt
) {}
