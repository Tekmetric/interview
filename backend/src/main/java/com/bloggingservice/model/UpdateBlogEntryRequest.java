package com.bloggingservice.model;

import java.time.Instant;

public record UpdateBlogEntryRequest(
        Instant creationTimestamp,
        String content) {}
