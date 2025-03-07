package com.interview.service;

import com.interview.dto.BookDto;
import com.interview.dto.PaginatedBooksDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    PaginatedBooksDto getAll(String keyword, Pageable pageable);
    BookDto findById(final long id);
    BookDto save(final BookDto bookDto);
    void delete(final long id);
}
