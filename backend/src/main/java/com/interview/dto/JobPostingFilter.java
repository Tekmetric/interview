package com.interview.dto;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * Optional query parameters accepted by GET /api/job-postings.
 * All fields are nullable — omitting a field means "no filter on that dimension".
 */
public record JobPostingFilter(
        @Parameter(description = "Return only remote (true) or on-site (false) roles", example = "true")
        Boolean remote,

        @Parameter(description = "Case-insensitive substring match on location field", example = "Houston")
        String location,

        @Parameter(description = "Case-insensitive substring match on job title", example = "Engineer")
        String titleContains
) {}