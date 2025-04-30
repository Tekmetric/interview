package com.interview.model.dto;

import com.interview.model.JobStatus;

import java.time.Instant;

public record JobUpdateRequest(JobStatus jobStatus,
                               Instant scheduledAt) {
}
