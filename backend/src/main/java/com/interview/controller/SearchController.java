package com.interview.controller;

import com.interview.dto.SearchResultDto;
import com.interview.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Search", description = "Unified search across artists, songs, and albums")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @ApiOperation(
            value = "Search across all entities",
            notes = "Unified search across artists, songs, and albums using a database view. " +
                    "Supports multiple search modes:\n" +
                    "1. General search (q parameter only): Searches across all entity types\n" +
                    "2. Filtered search (q + type parameters): Searches specific entity type (ARTIST, SONG, or ALBUM)\n" +
                    "3. Artist search (artist parameter): Finds all songs and albums by artist name\n" +
                    "Returns empty page if no search parameters provided."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully performed search (may return empty results)")
    })
    @GetMapping
    public ResponseEntity<Page<SearchResultDto>> search(
            @ApiParam(value = "Search query string (case-insensitive substring match)", example = "Queen")
            @RequestParam(required = false) String q,
            @ApiParam(value = "Entity type filter (ARTIST, SONG, or ALBUM)", example = "SONG")
            @RequestParam(required = false) String type,
            @ApiParam(value = "Search by artist name (finds all songs and albums by this artist)", example = "The Beatles")
            @RequestParam(required = false) String artist,
            @ApiParam(value = "Pagination parameters (page, size, sort)")
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
