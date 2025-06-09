package com.interview.model.dto;

import com.interview.model.JobStatus;

import java.time.Instant;
import java.util.List;

public record JobResponse(Integer id,
                          String vin,
                          String make,
                          String model,
                          int modelYear,
                          String customer,
                          Instant scheduledAt,
                          JobStatus status,
                          List<TaskResponse> tasks) {
}
