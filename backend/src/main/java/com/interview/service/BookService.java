package com.interview.service;

import com.interview.mapper.BookMapper;
import com.interview.repository.BookRepository;
import com.interview.repository.model.Book;
import com.interview.repository.model.Genre;
import com.interview.resource.model.BookDto;
import com.interview.resource.model.BookDto.BookResponse;
import com.interview.resource.model.BookDto.CreateRequest;
import com.interview.exception.BookStoreExceptions.BookNotFoundException;
import com.interview.exception.BookStoreExceptions.DuplicateIsbnException;
import com.interview.exception.BookStoreExceptions.OptimisticLockConflictException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional
    public BookResponse createBook(CreateRequest request) {
        log.debug("Creating book with ISBN={}", request.isbn());

        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }

        Book saved = bookRepository.save(bookMapper.toEntity(request));
        log.info("Created book id={} isbn={}", saved.getId(), saved.getIsbn());
        return bookMapper.toResponse(saved);
    }

    public BookResponse getBook(UUID id) {
        return bookRepository.findById(id)
                .map(bookMapper::toResponse)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Page<BookResponse> listBooks(
            String query,
            Genre genre,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        return bookRepository.search(query, genre, minPrice, maxPrice, pageable)
                .map(bookMapper::toResponse);
    }

    @Transactional
    public BookResponse updateBook(UUID id, BookDto.UpdateRequest request) {
        log.debug("Updating book id={} clientVersion={}", id, request.version());

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        if (bookRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw new DuplicateIsbnException(request.isbn());
        }

        try {
            bookMapper.updateEntity(request, book);
            Book saved = bookRepository.saveAndFlush(book);
            log.info("Updated book id={} newVersion={}", saved.getId(), saved.getVersion());
            return bookMapper.toResponse(saved);
        } catch (OptimisticLockException e) {
            log.error("OptimisticLockException during updating the book {}", book.getId(), e);
            throw new OptimisticLockConflictException(id, request.version(), book.getVersion());
        }
    }

    @Transactional
    public void deleteBook(UUID id) {
        log.debug("Deleting book id={}", id);
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
        log.info("Deleted book id={}", id);
    }
}