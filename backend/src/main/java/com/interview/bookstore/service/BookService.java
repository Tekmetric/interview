package com.interview.bookstore.service;

import com.interview.bookstore.domain.Author;
import com.interview.bookstore.domain.Book;
import com.interview.bookstore.domain.BookReview;
import com.interview.bookstore.domain.exception.DuplicateFieldException;
import com.interview.bookstore.domain.exception.ResourceNotFoundException;
import com.interview.bookstore.persistence.BookRepository;
import com.interview.bookstore.persistence.BookReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final AuthorService authorService;

    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Book> findDetailedById(Long id) {
        return bookRepository.findDetailedById(id);
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BookReview> findReviews(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException(Book.class, bookId);
        }
        return bookReviewRepository.findAllByBookId(bookId);
    }

    public BookReview addReview(Long bookId, BookReview review) {
        Optional<Book> reviewedBook = bookRepository.findById(bookId);
        return reviewedBook.map(book -> {
            review.setBook(book);
            return bookReviewRepository.save(review);
        })
        .orElseThrow(() -> new ResourceNotFoundException(Book.class, bookId));
    }

    public Book save(Long authorId, Book newBook) {
        var newIsbn = newBook.getBookDetails().getIsbn();
        bookRepository.findBookIdByIsbn(newIsbn)
                .ifPresent(id -> {
                    throw new DuplicateFieldException(Book.class, "ISBN");
                });

        Optional<Author> author = authorService.findById(authorId);
        return author.map(a -> {
            newBook.setAuthor(a);
            var bookDetails = newBook.getBookDetails();
            bookDetails.setBook(newBook);
            return bookRepository.save(newBook);
        })
        .orElseThrow(() -> new ResourceNotFoundException(Author.class, authorId));
    }

    public void update(Long bookId, Book updatedBook) {
       Optional<Book> retrievedBook = bookRepository.findById(bookId);
       var book = retrievedBook.orElseThrow(() -> new ResourceNotFoundException(Book.class, bookId));

       book.setTitle(updatedBook.getTitle());
       book.setPrice(updatedBook.getPrice());
       var bookDetails = book.getBookDetails();
       bookDetails.setDescription(updatedBook.getBookDetails().getDescription());
       bookDetails.setPageCount(updatedBook.getBookDetails().getPageCount());

       bookRepository.save(book);
    }

    public void delete(Long bookId) {
        bookRepository.deleteById(bookId);
    }

}
