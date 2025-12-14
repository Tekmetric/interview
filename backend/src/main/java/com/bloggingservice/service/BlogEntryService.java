package com.bloggingservice.service;

import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

public interface BlogEntryService {
    BlogEntryResponse createBlogEntry(CreateBlogEntryRequest request);

    Page<BlogEntryResponse> getBlogEntries(Pageable pageable);

    BlogEntryResponse getBlogEntry(UUID id) throws NoResourceFoundException;

    BlogEntryResponse updateBlogEntry(UUID id, UpdateBlogEntryRequest request) throws NoResourceFoundException;

    void removeBlogEntry(UUID id);
}
