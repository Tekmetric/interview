package com.bloggingservice.service;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import com.bloggingservice.model.mapper.BlogEntryMapper;
import com.bloggingservice.repository.BlogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    public BlogEntryResponse getBlogEntry(UUID id) throws NoResourceFoundException {
        return blogEntryRepository.findById(id)
                .map(blogEntryMapper::toBlogEntryResponse)
                .orElseThrow(() -> getNoResourceFoundException(HttpMethod.GET));
    }

    @Override
    public Page<BlogEntryResponse> getBlogEntries(Pageable page) {
        return blogEntryRepository.findAll(page)
                .map(blogEntryMapper::toBlogEntryResponse);
    }

    @Transactional
    @Override
    public  BlogEntryResponse updateBlogEntry(UUID id, UpdateBlogEntryRequest request) throws  NoResourceFoundException {
        return blogEntryRepository.findById(id)
                .map(entity -> blogEntryMapper.fromUpdateRequest(entity, request))
                .map(blogEntryRepository::save)
                .map(blogEntryMapper::toBlogEntryResponse)
                .orElseThrow(() -> getNoResourceFoundException(HttpMethod.PATCH));
    }

    @Override
    public void removeBlogEntry(UUID id) {
        blogEntryRepository.deleteById(id);
    }

    private NoResourceFoundException getNoResourceFoundException(HttpMethod method) {
                return new NoResourceFoundException(method, "/api/v1/blog-entry/{id}");
    }
}
