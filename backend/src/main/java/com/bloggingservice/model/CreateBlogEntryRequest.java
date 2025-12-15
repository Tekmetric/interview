package com.bloggingservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@Schema(
    name = "Create Blog Entry Request",
    description = "The request body used to create a blog entry")
public record CreateBlogEntryRequest(
    @NotNull(message = "Cannot be null") Instant creationTimestamp,
    @NotBlank(message = "Cannot be empty or blank") String content,
    Set<CategoryType> categories) {}
