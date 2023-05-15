package com.interview.service;

import com.interview.domain.Book;
import com.interview.domain.Review;
import com.interview.dto.*;
import com.interview.exception.DuplicateResourceException;
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
@Transactional
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

    public IdWrapperDto createBooks(BookDto bookDto) {
        validateBookIsUnique(bookDto);
        Book book = bookMapper.toEntity(bookDto);
        bookRepository.save(book);
        return new IdWrapperDto(book.getId());
    }

    public void updateBook(BookDto bookDto) {
        validateBookIsUnique(bookDto);
        Book book = findBook(bookDto.getId());
        book.setAuthor(bookDto.getAuthor());
        book.setTitle(bookDto.getTitle());
    }

    public void deleteBook(Long id) {
        validateBookExists(id);
        bookRepository.deleteById(id);
    }

    public void addReview(Long id, ReviewDto reviewDto) {
        Book book = findBook(id);
        Review review = reviewMapper.toEntity(reviewDto);
        book.addReview(review);
    }

    public void deleteReview(Long id) {
        validateReviewExists(id);
        reviewRepository.deleteById(id);
    }

    private Book findBook(Long id) {
        return bookRepository.findByIdWithReviews(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find book with id: " + id));
    }

    private void validateBookExists(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot find book with id: " + id);
        }
    }

    private void validateReviewExists(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot find review with id: " + id);
        }
    }

    private void validateBookIsUnique(BookDto bookDto) {
        if (bookDto.getId() != null && bookRepository.existsByTitleAndAuthorAndIdNot(bookDto.getTitle(), bookDto.getAuthor(), bookDto.getId())) {
            throw new DuplicateResourceException(String.format("Book with title: '%s' and author: '%s' already exists.", bookDto.getTitle(), bookDto.getAuthor()));
        }

        if (bookDto.getId() == null && bookRepository.existsByTitleAndAuthor(bookDto.getTitle(), bookDto.getAuthor())) {
            throw new DuplicateResourceException(String.format("Book with title: '%s' and author: '%s' already exists.", bookDto.getTitle(), bookDto.getAuthor()));
        }
    }

}
