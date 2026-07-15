package com.interview.model.dto;

import com.interview.model.enums.TaskPriority;
import com.interview.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Data transfer object for creating a task or performing a full update (PUT).
 *
 * <p>Contains bean validation constraints enforced when used
 * with {@code @Valid} in controller methods. All required fields must be provided.</p>
 */
public record TaskRequest(
        @NotBlank(message = "Task key is required")
        @Size(max = 20, message = "Task key must not exceed 20 characters")
        String taskKey,

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        String description,

        TaskStatus status,

        TaskPriority priority,

        @Positive(message = "Story points must be a positive number")
        Integer storyPoints,

        Long reporterId,

        Long assigneeId,

        Set<Long> tagIds
) {}
