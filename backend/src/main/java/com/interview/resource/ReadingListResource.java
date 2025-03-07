package com.interview.resource;

import com.interview.dto.*;
import com.interview.entity.User;
import com.interview.dto.ReadingListRequestDto;
import com.interview.service.ReadingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reading-lists")
public class ReadingListResource {
    private static final Logger logger = LoggerFactory.getLogger(ReadingListResource.class);

    private final ReadingListService readingListService;

    public ReadingListResource(ReadingListService readingListService) {
        this.readingListService = readingListService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaginatedReadingListDto> getAll(@AuthenticationPrincipal UserDetails userDetails,
                                                          String keyword,
                                                          Pageable pageable) {
        logger.debug("Searching for reading lists of {} with keyword {}", userDetails.getUsername(), keyword);
        PaginatedReadingListDto readingLists = readingListService.getAll(keyword, userDetails.getUsername(), pageable);
        logger.debug("Returning {}/{} of {} reading lists of {}.",
                readingLists.getCurrentPage() + 1,
                readingLists.getTotalPages(),
                readingLists.getTotalItems(),
                userDetails.getUsername());
        return ResponseEntity.ok(readingLists);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReadingListDto> getById(@PathVariable final long id) {
        logger.debug("Searching for reading list with id {}", id);
        ReadingListDto readingListDto = readingListService.findById(id);
        if (readingListDto == null) {
            logger.debug("Reading list not found by id: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Returning reading list: {}", readingListDto);
        return ResponseEntity.ok(readingListDto);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReadingListDto> save(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody @Valid final ReadingListRequestDto readingListRequestDto) {
        ReadingListDto readingListDtoSaved = readingListService.save(userDetails.getUsername(), readingListRequestDto);
        return ResponseEntity.ok(readingListDtoSaved);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReadingListDto> update(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody @Valid final ReadingListRequestDto readingListRequestDto) {
        logger.debug("Updating reading list of {}: {}", userDetails.getUsername(), readingListRequestDto);
        ReadingListDto readingListDtoUpdated = readingListService.save(userDetails.getUsername(), readingListRequestDto);
        if (readingListDtoUpdated != null) {
            return ResponseEntity.ok(readingListDtoUpdated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails userDetails,
                                       @PathVariable final long id) {
        logger.debug("Deleting reading list by id: {}", id);
        readingListService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/shared", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedReadingListDto> getAllShared(String keyword, Pageable pageable) {
        logger.debug("Searching for shared reading lists with keyword {}", keyword);
        PaginatedReadingListDto sharedReadingLists = readingListService.getAllShared(keyword, pageable);
        logger.debug("Returning {}/{} of {} shared reading lists.",
                sharedReadingLists.getCurrentPage() + 1,
                sharedReadingLists.getTotalPages(),
                sharedReadingLists.getTotalItems());
        return ResponseEntity.ok(sharedReadingLists);
    }


}
