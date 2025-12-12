package com.bloggingservice.service;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.mapper.BlogEntryMapper;
import com.bloggingservice.repository.BlogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BlogEntryServiceImpl implements BlogEntryService {

    private final BlogEntryMapper blogEntryMapper;
    private final BlogEntryRepository blogEntryRepository;

    @Override
    public BlogEntryResponse createBlogEntry(CreateBlogEntryRequest request) {
        final BlogEntryEntity blogEntry = blogEntryRepository.save(blogEntryMapper.fromCreateRequest(request));

        return blogEntryMapper.toBlogEntryResponse(blogEntry);
    }

    @Override
    public BlogEntryResponse getBlogEntry(UUID id) {
        return blogEntryRepository.findById(id)
                .map(blogEntryMapper::toBlogEntryResponse)
                .orElseThrow();
    }
}
