package com.interview.controller;

import com.interview.dto.SearchResultDto;
import com.interview.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<Page<SearchResultDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String artist,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<SearchResultDto> results;

        if (artist != null && !artist.trim().isEmpty()) {
            // Search by artist name
            results = searchService.searchByArtist(artist, pageable);
        } else if (type != null && !type.trim().isEmpty() && q != null && !q.trim().isEmpty()) {
            // Search by entity type and query
            results = searchService.searchByType(q, type.toUpperCase(), pageable);
        } else if (q != null && !q.trim().isEmpty()) {
            // General search
            results = searchService.search(q, pageable);
        } else {
            // Return empty result if no search parameters provided
            results = Page.empty(pageable);
        }

        return ResponseEntity.ok(results);
    }
}
