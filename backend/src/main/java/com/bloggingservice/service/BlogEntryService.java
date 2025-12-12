package com.bloggingservice.service;

import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.BlogEntryResponse;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

public interface BlogEntryService {
    BlogEntryResponse createBlogEntry(CreateBlogEntryRequest request);

    BlogEntryResponse getBlogEntry(UUID id) throws NoResourceFoundException;
}
