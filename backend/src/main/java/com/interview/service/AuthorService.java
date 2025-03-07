package com.interview.service;

import com.interview.dto.AuthorDto;
import com.interview.dto.BookDto;
import com.interview.dto.PaginatedAuthorsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {
    PaginatedAuthorsDto getAll(String keyword, Pageable pageable);
    AuthorDto findById(final long id);
    List<BookDto> findBooksOfAuthorById(long id);
    AuthorDto save(final AuthorDto authorDto);
    AuthorDto update(final AuthorDto authorDto);
    void delete(final long id);

}
