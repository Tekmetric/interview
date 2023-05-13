package com.interview.service;

import com.interview.domain.Book;
import com.interview.dto.BookDto;
import com.interview.dto.PageRequestDto;
import com.interview.dto.PageResponseDto;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.BookMapper;
import com.interview.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookDto getBook(Long id) {
        Book book = bookRepository.findByIdWithReviews(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find book with id: " + id));
        return bookMapper.toDto(book);
    }

    public PageResponseDto<BookDto> getBooks(PageRequestDto pageRequestDto) {
        Page<Book> books = bookRepository.findAll(PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getPageSize()));
        return bookMapper.toDto(books);
    }
}
