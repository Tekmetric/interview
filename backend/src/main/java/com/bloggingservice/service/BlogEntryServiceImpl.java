package com.bloggingservice.service;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.CreateBlogEntryResponse;
import com.bloggingservice.model.mapper.BlogEntryMapper;
import com.bloggingservice.repository.BlogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlogEntryServiceImpl implements BlogEntryService {

    private final BlogEntryMapper blogEntryMapper;
    private final BlogEntryRepository blogEntryRepository;

    @Override
    public CreateBlogEntryResponse createBlogEntry(CreateBlogEntryRequest request) {
        final BlogEntryEntity blogEntry = blogEntryRepository.save(blogEntryMapper.fromCreateRequest(request));

        return blogEntryMapper.toBlogEntryResponse(blogEntry);
    }
}
