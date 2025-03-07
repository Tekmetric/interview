package com.interview.resource;

import com.interview.dto.BookDto;
import com.interview.dto.PaginatedBooksDto;
import com.interview.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@PreAuthorize("isAuthenticated()")
public class BookResource {
    private static final Logger logger = LoggerFactory.getLogger(BookResource.class);
    private final BookService bookService;

    public BookResource(final BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedBooksDto> getAll(String keyword, Pageable pageable) {
        logger.debug("Request to get all books. Keyword: {}, pageable: {} ", keyword, pageable);
        PaginatedBooksDto books = bookService.getAll(keyword, pageable);
        logger.debug("Returning {}. page of {} total pages of {} books.",
                books.getCurrentPage() + 1,
                books.getTotalPages(),
                books.getTotalItems());
        return ResponseEntity.ok(books);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/{id}")
    public ResponseEntity<BookDto> getById(@PathVariable final long id) {
        logger.debug("Searching for book with id {}", id);
        BookDto bookDto = bookService.findById(id);
        if (bookDto == null) {
            logger.debug("Book not found by id: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Returning book: {}", bookDto);
        return ResponseEntity.ok(bookDto);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('' + T(com.interview.entity.User).AUTHORITY_NAME_ADMIN)")
    public ResponseEntity<BookDto> save(@RequestBody @Valid final BookDto bookDto) {
        logger.debug("Saving book: {}", bookDto);
        return ResponseEntity.ok(bookService.save(bookDto));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('' + T(com.interview.entity.User).AUTHORITY_NAME_ADMIN)")
    public ResponseEntity<BookDto> update(@RequestBody @Valid final BookDto bookDto) {
        logger.debug("Updating book: {}", bookDto);
        BookDto bookDtoUpdated = bookService.save(bookDto);
        if (bookDtoUpdated != null) {
            return ResponseEntity.ok(bookDtoUpdated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('' + T(com.interview.entity.User).AUTHORITY_NAME_ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable final long id) {
        logger.debug("Deleting book by id: {}", id);
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
