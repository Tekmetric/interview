package com.bloggingservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateBlogEntryRequest(
        @NotNull(message = "Cannot be null") Instant creationTimestamp,
        @NotBlank(message = "Cannot be empty or blank") String content) {}
