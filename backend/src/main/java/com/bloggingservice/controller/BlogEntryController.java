package com.bloggingservice.controller;

import com.bloggingservice.model.BlogEntryId;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import com.bloggingservice.service.BlogEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/blog-entry", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class BlogEntryController {

    private static final Set<String> VALID_PAGE_SORT_FIELDS = Set.of("creationTimestamp");

    private final BlogEntryService blogEntryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlogEntryResponse createBlogEntry(Principal principal, @Valid @RequestBody CreateBlogEntryRequest request) {
        return blogEntryService.createBlogEntry(principal, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BlogEntryResponse> getBlogEntries(Principal principal, Pageable pageable) {
        pageable.getSort().stream().forEach(order -> {
            if (!VALID_PAGE_SORT_FIELDS.contains(order.getProperty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("%s is not an acceptable sort field from %s", order.getProperty(), VALID_PAGE_SORT_FIELDS));
            }
        });
        return blogEntryService.getBlogEntries(principal, pageable);
    }


    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlogEntryResponse getBlogEntry(Principal principal, @PathVariable UUID id) throws NoResourceFoundException {
        return blogEntryService.getBlogEntry(new BlogEntryId(id, principal.getName()));
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlogEntryResponse updateBlogEntry(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBlogEntryRequest request) throws NoResourceFoundException {
        return blogEntryService.updateBlogEntry(new BlogEntryId(id, principal.getName()), request);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBlogEntry(Principal principal, @PathVariable UUID id) {
        blogEntryService.removeBlogEntry(new BlogEntryId(id, principal.getName()));
    }
}
