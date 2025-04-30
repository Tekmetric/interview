package com.interview.model.dto;

import com.interview.model.TaskStatus;

public record TaskResponse(Integer id,
                           Integer jobId,
                           TaskStatus status,
                           String title,
                           String type,
                           String description,
                           String mechanicName) {
}
