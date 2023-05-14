package com.interview.service;

import com.interview.domain.Book;
import com.interview.domain.Review;
import com.interview.dto.*;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.BookMapper;
import com.interview.mapper.ReviewMapper;
import com.interview.repository.BookRepository;
import com.interview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final BookMapper bookMapper;
    private final ReviewMapper reviewMapper;

    public BookDto getBook(Long id) {
        Book book = findBook(id);
        return bookMapper.toDto(book);
    }

    public PageResponseDto<BookDto> getBooks(PageRequestDto pageRequestDto) {
        Page<Book> books = bookRepository.findAll(PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getPageSize()));
        return bookMapper.toDto(books);
    }

    @Transactional
    public IdWrapperDto createBooks(BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        bookRepository.save(book);
        return new IdWrapperDto(book.getId());
    }

    @Transactional
    public void updateBook(Long id, BookDto bookDto) {
        Book book = findBook(id);
        book.setAuthor(bookDto.getAuthor());
        book.setTitle(bookDto.getTitle());
    }

    @Transactional
    public void deleteBook(Long id) {
        checkIfBookExists(id);
        bookRepository.deleteById(id);
    }

    @Transactional
    public void addReview(Long id, ReviewDto reviewDto) {
        Book book = findBook(id);
        Review review = reviewMapper.toEntity(reviewDto);
        book.addReview(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        checkIfReviewExists(id);
        reviewRepository.deleteById(id);
    }

    private Book findBook(Long id) {
        return bookRepository.findByIdWithReviews(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find book with id: " + id));
    }

    private void checkIfBookExists(Long id) {
        boolean exists = bookRepository.existsById(id);

        if (!exists) {
            throw new ResourceNotFoundException("Cannot find book with id: " + id);
        }
    }

    private void checkIfReviewExists(Long id) {
        boolean exists = reviewRepository.existsById(id);

        if (!exists) {
            throw new ResourceNotFoundException("Cannot find review with id: " + id);
        }
    }

}
