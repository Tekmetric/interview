package com.interview.model.dto;

import com.interview.model.TaskStatus;

public record TaskRequest(Integer jobId,
                          TaskStatus status,
                          String title,
                          String type,
                          String description,
                          String mechanicName) {
}
