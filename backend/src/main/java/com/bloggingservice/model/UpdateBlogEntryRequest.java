package com.bloggingservice.model;

import java.time.Instant;
import java.util.Set;

public record UpdateBlogEntryRequest(
        Instant creationTimestamp,
        String content,
        Set<CategoryType> categories) {}
