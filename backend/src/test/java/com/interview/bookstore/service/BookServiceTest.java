package com.interview.bookstore.service;

import com.interview.bookstore.domain.Author;
import com.interview.bookstore.domain.Book;
import com.interview.bookstore.domain.BookDetails;
import com.interview.bookstore.domain.BookReview;
import com.interview.bookstore.domain.exception.DuplicateFieldException;
import com.interview.bookstore.domain.exception.ResourceNotFoundException;
import com.interview.bookstore.persistence.BookRepository;
import com.interview.bookstore.persistence.BookReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private static final Long BOOK_ID = 1L;
    private static final Long AUTHOR_ID = 10L;
    private static final String ISBN = "0-545-01022-5";
    private static final String ISBN_FIELD = "ISBN";

    @Mock private BookRepository bookRepository;
    @Mock private BookReviewRepository bookReviewRepository;
    @Mock private AuthorService authorService;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        this.bookService = new BookService(bookRepository, bookReviewRepository, authorService);
    }

    @Test
    void findReviews_throwsExceptionWhenBooksNotFound() {
        when(bookRepository.existsById(eq(BOOK_ID)))
                .thenReturn(false);

        var expectedException = catchThrowableOfType(
                () -> bookService.findReviews(BOOK_ID), ResourceNotFoundException.class);

        assertThat(expectedException.getResourceType()).isEqualTo(Book.class);
        assertThat(expectedException.getResourceId()).isEqualTo(BOOK_ID);
    }

    @Test
    void addBookReview_throwsExceptionWhenBookNotFound() {
        when(bookRepository.findById(eq(BOOK_ID)))
                .thenReturn(Optional.empty());

        var reviewToSave = new BookReview();
        var expectedException = catchThrowableOfType(
                () -> bookService.addReview(BOOK_ID, reviewToSave), ResourceNotFoundException.class);

        assertThat(expectedException.getResourceType()).isEqualTo(Book.class);
        assertThat(expectedException.getResourceId()).isEqualTo(BOOK_ID);
    }

    @Test
    void saveBook_throwsExceptionWhenISBNIsDuplicated() {
        when(bookRepository.findBookIdByIsbn(eq(ISBN)))
                .thenReturn(Optional.of(BOOK_ID));

        var newBook = new Book();
        var newBookDetails = new BookDetails();
        newBookDetails.setIsbn(ISBN);
        newBook.setBookDetails(newBookDetails);

        var expectedException = catchThrowableOfType(
                () -> bookService.save(AUTHOR_ID, newBook), DuplicateFieldException.class);

        assertThat(expectedException.getResourceType()).isEqualTo(Book.class);
        assertThat(expectedException.getFieldName()).isEqualTo(ISBN_FIELD);
    }

    @Test
    void saveBook_throwsExceptionWhenAuthorNotFound() {
        when(bookRepository.findBookIdByIsbn(eq(ISBN)))
                .thenReturn(Optional.empty());
        when(authorService.findById(eq(AUTHOR_ID)))
                .thenReturn(Optional.empty());

        var newBook = new Book();
        var newBookDetails = new BookDetails();
        newBookDetails.setIsbn(ISBN);
        newBook.setBookDetails(newBookDetails);

        var expectedException = catchThrowableOfType(
                () -> bookService.save(AUTHOR_ID, newBook), ResourceNotFoundException.class);

        assertThat(expectedException.getResourceType()).isEqualTo(Author.class);
        assertThat(expectedException.getResourceId()).isEqualTo(AUTHOR_ID);
    }

    @Test
    void updateBook_throwsExceptionWhenBookNotFound() {
        when(bookRepository.findById(eq(BOOK_ID)))
                .thenReturn(Optional.empty());

        var updatedBook = new Book();
        var expectedException = catchThrowableOfType(
                () -> bookService.update(BOOK_ID, updatedBook), ResourceNotFoundException.class);

        assertThat(expectedException.getResourceType()).isEqualTo(Book.class);
        assertThat(expectedException.getResourceId()).isEqualTo(BOOK_ID);
    }
}
