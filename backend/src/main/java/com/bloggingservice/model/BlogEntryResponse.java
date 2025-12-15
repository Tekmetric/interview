package com.bloggingservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Schema(name = "Blog Entry Response", description = "The response given for a single blog entry")
public record BlogEntryResponse(
    UUID id,
    Instant creationTimestamp,
    Instant lastUpdateTimestamp,
    String content,
    Set<CategoryType> categories) {}
