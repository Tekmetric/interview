package com.interview.service.impl;

import com.interview.dto.BookDto;
import com.interview.dto.PaginatedBooksDto;
import com.interview.entity.Book;
import com.interview.repository.BookRepository;
import com.interview.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public PaginatedBooksDto getAll(String keyword, Pageable pageable) {
        logger.debug("Getting all books from DB. Keyword: {}, pageable: {} ", keyword, pageable);

        String keywordParam = StringUtils.hasLength(keyword) ? keyword.toUpperCase() : null;

        Page<Book> pageOfBooks = bookRepository.findAllByKeyword(keywordParam, pageable);

        return new PaginatedBooksDto(pageable.getPageNumber(),
                pageOfBooks.getTotalPages(),
                pageOfBooks.getTotalElements(),
                pageOfBooks.getContent()
                        .stream()
                        .map(BookDto::new)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public BookDto findById(final long id) {
        logger.debug("Finding book by id {}", id);
        return bookRepository.findById(id).map(BookDto::new).orElse(null);
    }

    @Override
    public BookDto save(final BookDto bookDto) {
        logger.debug("Saving book {}", bookDto);

        Book book = new Book(bookDto);
        bookRepository.save(book);
        return new BookDto(book);
    }

    @Override
    @Transactional
    public void delete(final long id) {
        logger.debug("Deleting book by id {}", id);
        bookRepository.deleteById(id);
    }
}
