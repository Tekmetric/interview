package com.bloggingservice.service;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.BlogEntryId;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import com.bloggingservice.model.mapper.BlogEntryMapper;
import com.bloggingservice.repository.BlogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BlogEntryServiceImpl implements BlogEntryService {

    private final BlogEntryMapper blogEntryMapper;
    private final BlogEntryRepository blogEntryRepository;

    @Override
    public BlogEntryResponse createBlogEntry(Principal principal, CreateBlogEntryRequest request) {
        final BlogEntryEntity blogEntry = blogEntryRepository.save(
                blogEntryMapper.fromCreateRequest(principal.getName(), request));

        return blogEntryMapper.toBlogEntryResponse(blogEntry);
    }

    @Override
    public BlogEntryResponse getBlogEntry(BlogEntryId id) throws NoResourceFoundException {
        return blogEntryRepository.findById(id)
                .map(blogEntryMapper::toBlogEntryResponse)
                .orElseThrow(() -> getNoResourceFoundException(HttpMethod.GET));
    }

    @Override
    public Page<BlogEntryResponse> getBlogEntries(Principal principal, Pageable page) {
        String author = principal.getName();
        Page<UUID> pageIds = blogEntryRepository.findAllIdsByAuthor(author, page);
        List<BlogEntryId> ids = pageIds.getContent().stream()
                .map(id -> new BlogEntryId(id, author))
                .toList();

        List<BlogEntryResponse> response = blogEntryRepository.findAllById(ids)
                .stream().map(blogEntryMapper::toBlogEntryResponse)
                .toList();

        return new PageImpl<>(response, page, pageIds.getTotalElements());
    }

    @Transactional
    @Override
    public  BlogEntryResponse updateBlogEntry(BlogEntryId id, UpdateBlogEntryRequest request) throws  NoResourceFoundException {
        return blogEntryRepository.findById(id)
                .map(entity -> blogEntryMapper.fromUpdateRequest(entity, request))
                .map(blogEntryRepository::save)
                .map(blogEntryMapper::toBlogEntryResponse)
                .orElseThrow(() -> getNoResourceFoundException(HttpMethod.PATCH));
    }

    @Override
    public void removeBlogEntry(BlogEntryId id) {
        blogEntryRepository.deleteById(id);
    }

    private NoResourceFoundException getNoResourceFoundException(HttpMethod method) {
                return new NoResourceFoundException(method, "/api/v1/blog-entry/{id}");
    }
}
