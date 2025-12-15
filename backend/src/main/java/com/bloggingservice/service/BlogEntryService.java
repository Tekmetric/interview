package com.bloggingservice.service;

import com.bloggingservice.model.BlogEntryId;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.resource.NoResourceFoundException;

public interface BlogEntryService {
  BlogEntryResponse createBlogEntry(String author, CreateBlogEntryRequest request);

  Page<BlogEntryResponse> getBlogEntries(String author, Pageable pageable);

  BlogEntryResponse getBlogEntry(BlogEntryId id) throws NoResourceFoundException;

  BlogEntryResponse updateBlogEntry(BlogEntryId id, UpdateBlogEntryRequest request)
      throws NoResourceFoundException;

  void removeBlogEntry(BlogEntryId Id);
}
