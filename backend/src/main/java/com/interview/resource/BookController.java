package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.exception.BookStoreExceptions.InvalidSortParameterException;
import com.interview.repository.model.Genre;
import com.interview.resource.model.BookDto.BookResponse;
import com.interview.resource.model.BookDto.CreateRequest;
import com.interview.resource.model.BookDto.UpdateRequest;
import com.interview.service.BookService;
import com.interview.service.IdempotencyService;
import com.interview.service.IdempotencyService.CachedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Books", description = "CRUD operations for the book catalogue")
public class BookController {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final Set<String> allowedSortFields = Set.of("title", "author", "price");

    private final BookService bookService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a book",
            description = "Creates a new book. Supply `Idempotency-Key` to make the operation safe to retry.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Book created"),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Duplicate ISBN")
            }
    )
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody CreateRequest request,
            @RequestHeader(value = IDEMPOTENCY_KEY_HEADER, required = false) String idempotencyKey) {

        if (idempotencyKey != null) {
            Optional<BookResponse> cached = replayAs(idempotencyKey, BookResponse.class);
            if (cached.isPresent()) {
                log.debug("Returning cached response for idempotencyKey : {}", idempotencyKey);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(cached.get());
            }
        }

        BookResponse response = bookService.createBook(request);

        if (idempotencyKey != null) {
            idempotencyService.store(idempotencyKey, HttpStatus.CREATED.value(), response);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a book by ID")
    public ResponseEntity<BookResponse> getBook(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List books",
            description = "Returns a paginated, filterable list. Sort by author, price or title field, e.g. `?sort=price,asc`."
    )
    public ResponseEntity<Page<BookResponse>> listBooks(
            @Parameter(description = "Free-text search on title or author")
            @RequestParam(required = false) String query,
            @Parameter(description = "Filter by genre")
            @RequestParam(required = false) Genre genre,
            @Parameter(description = "Minimum price (inclusive)")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price (inclusive)")
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable) {

        pageable.getSort()
                .forEach(order -> {
                    if (!allowedSortFields.contains(order.getProperty())) {
                        throw new InvalidSortParameterException(order.getProperty());
                    }
                });

        return ResponseEntity.ok(bookService.listBooks(query, genre, minPrice, maxPrice, pageable));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Update a book (full replacement)",
            description = """
                    Replaces all fields. The request body must include the current `version`
                    for optimistic concurrency control. A stale version returns 409 Conflict —
                    fetch the latest state and retry.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book updated"),
                    @ApiResponse(responseCode = "404", description = "Book not found"),
                    @ApiResponse(responseCode = "409", description = "Version conflict or duplicate ISBN")
            }
    )
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRequest request,
            @RequestHeader(value = IDEMPOTENCY_KEY_HEADER, required = false) String idempotencyKey) {

        if (idempotencyKey != null) {
            Optional<BookResponse> cached = replayAs(idempotencyKey, BookResponse.class);
            if (cached.isPresent()) {
                log.debug("Returning cached response for idempotencyKey : {}", idempotencyKey);
                return ResponseEntity.ok(cached.get());
            }
        }

        BookResponse response = bookService.updateBook(id, request);

        if (idempotencyKey != null) {
            idempotencyService.store(idempotencyKey, HttpStatus.OK.value(), response);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a book",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Book deleted"),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent()
                .build();
    }

    private <T> Optional<T> replayAs(String idempotencyKey, Class<T> type) {
        return idempotencyService.find(idempotencyKey)
                .map(cached -> deserialise(cached, type));
    }

    private <T> T deserialise(CachedResponse cached, Class<T> type) {
        try {
            return objectMapper.readValue(cached.bodyJson(), type);
        } catch (Exception e) {
            log.warn("Failed to deserialise cached idempotency response for type={}", type.getSimpleName(), e);
            throw new IllegalStateException("Cached idempotency response could not be deserialised", e);
        }
    }
}