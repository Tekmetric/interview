package com.bloggingservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateBlogEntryRequest(@NotNull Instant creationTimestamp, @NotBlank(message = "Content cannot be empty or blank") String content) {}
