package com.interview.resource;

import com.interview.dto.AuthorDto;
import com.interview.dto.BookDto;
import com.interview.dto.PaginatedAuthorsDto;
import com.interview.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@PreAuthorize("isAuthenticated()")
public class AuthorResource {
    private static final Logger logger = LoggerFactory.getLogger(AuthorResource.class);
    private final AuthorService authorService;

    public AuthorResource(final AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedAuthorsDto> getAll(String keyword, Pageable pageable) {
        logger.debug("Searching for authors with keyword {}", keyword);
        PaginatedAuthorsDto authors = authorService.getAll(keyword, pageable);
        logger.debug("Returning {}. page of {} total pages of {} authors.",
                authors.getCurrentPage() + 1,
                authors.getTotalPages(),
                authors.getTotalItems());
        return ResponseEntity.ok(authors);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/{id}")
    public ResponseEntity<AuthorDto> getById(@PathVariable final long id) {
        logger.debug("Searching for author with id {}", id);
        AuthorDto authorDto = authorService.findById(id);
        if (authorDto == null) {
            logger.debug("Author not found by id: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Returning author: {}", authorDto);
        return ResponseEntity.ok(authorDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/{id}/books")
    public ResponseEntity<List<BookDto>> getBooksById(@PathVariable final long id) {
        logger.debug("Searching books of author with id {}", id);
        AuthorDto authorDto = authorService.findById(id);
        if (authorDto == null) {
            logger.debug("Author not found by id: {}", id);
            return ResponseEntity.notFound().build();
        }
        List<BookDto> booksOfAuthor = authorService.findBooksOfAuthorById(authorDto.getId());
        logger.debug("Returning books of author: {}", booksOfAuthor.size());
        return ResponseEntity.ok(booksOfAuthor);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('' + T(com.interview.entity.User).AUTHORITY_NAME_ADMIN)")
    public ResponseEntity<AuthorDto> save(@RequestBody @Valid final AuthorDto authorDto) {
        logger.debug("Saving author: {}", authorDto);
        AuthorDto savedAuthorDto = authorService.save(authorDto);
        return ResponseEntity.ok(savedAuthorDto);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('' + T(com.interview.entity.User).AUTHORITY_NAME_ADMIN)")
    public ResponseEntity<AuthorDto> update(@RequestBody @Valid final AuthorDto authorDto) {
        logger.debug("Updating author: {}", authorDto);
        AuthorDto authorDtoUpdated = authorService.update(authorDto);
        if (authorDtoUpdated != null) {
            return ResponseEntity.ok(authorDtoUpdated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('' + T(com.interview.entity.User).AUTHORITY_NAME_ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable final long id) {
        logger.debug("Deleting author with id: {}", id);
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
