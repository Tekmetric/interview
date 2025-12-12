package com.bloggingservice.service;

import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.BlogEntryResponse;

import java.util.UUID;

public interface BlogEntryService {
    BlogEntryResponse createBlogEntry(CreateBlogEntryRequest request);

    BlogEntryResponse getBlogEntry(UUID id);
}
