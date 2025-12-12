package com.bloggingservice.controller;

import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.CreateBlogEntryResponse;
import com.bloggingservice.service.BlogEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/blog-entry", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BlogEntryController {

    private final BlogEntryService blogEntryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateBlogEntryResponse createBlogEntry(@Valid @RequestBody CreateBlogEntryRequest request) {
        return blogEntryService.createBlogEntry(request);
    }
}
