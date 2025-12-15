package com.bloggingservice.service;

import com.bloggingservice.model.BlogEntryId;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.Principal;

public interface BlogEntryService {
    BlogEntryResponse createBlogEntry(Principal principal, CreateBlogEntryRequest request);

    Page<BlogEntryResponse> getBlogEntries(Principal principal, Pageable pageable);

    BlogEntryResponse getBlogEntry(BlogEntryId id) throws NoResourceFoundException;

    BlogEntryResponse updateBlogEntry(BlogEntryId id, UpdateBlogEntryRequest request) throws NoResourceFoundException;

    void removeBlogEntry(BlogEntryId Id);
}
