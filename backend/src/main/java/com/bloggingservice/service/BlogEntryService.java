package com.bloggingservice.service;

import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.CreateBlogEntryResponse;

public interface BlogEntryService {
    CreateBlogEntryResponse createBlogEntry(CreateBlogEntryRequest request);
}
