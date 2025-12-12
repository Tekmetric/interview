package com.bloggingservice.model;

import java.time.Instant;
import java.util.UUID;

public record CreateBlogEntryResponse(UUID id, Instant creationTimestamp, String content) {}
