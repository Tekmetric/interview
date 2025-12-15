package com.bloggingservice.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record BlogEntryResponse(UUID id, Instant creationTimestamp, String content, Set<CategoryType> categories) {}
