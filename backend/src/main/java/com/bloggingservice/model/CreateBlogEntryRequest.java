package com.bloggingservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;

public record CreateBlogEntryRequest(
        @NotNull(message = "Cannot be null") Instant creationTimestamp,
        @NotBlank(message = "Cannot be empty or blank") String content,
        Set<CategoryType> categories) {}

