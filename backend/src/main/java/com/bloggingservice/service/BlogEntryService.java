package com.bloggingservice.service;

import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

public interface BlogEntryService {
    BlogEntryResponse createBlogEntry(CreateBlogEntryRequest request);

    BlogEntryResponse getBlogEntry(UUID id) throws NoResourceFoundException;

    BlogEntryResponse updateBlogEntry(UUID id, UpdateBlogEntryRequest request) throws NoResourceFoundException;
}
