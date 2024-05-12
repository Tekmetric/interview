package com.interview.bookstore.api;

import com.interview.bookstore.api.dto.BookDTO;
import com.interview.bookstore.api.dto.BookReviewDTO;
import com.interview.bookstore.api.dto.DetailedBookDTO;
import com.interview.bookstore.api.dto.NewBookDTO;
import com.interview.bookstore.api.dto.NewBookReviewDTO;
import com.interview.bookstore.api.dto.UpdateBookDTO;
import com.interview.bookstore.api.dto.mapper.BookMapper;
import com.interview.bookstore.api.dto.mapper.ReviewMapper;
import com.interview.bookstore.domain.Book;
import com.interview.bookstore.domain.exception.ResourceNotFoundException;
import com.interview.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        var books =  bookService.findAll().stream()
                .map(BookMapper::toDTO)
                .toList();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<? extends BookDTO> getBook(@PathVariable Long bookId,
                                                     @RequestParam(required = false) boolean detailed) {
        Optional<Book> retrievedBook = detailed ? bookService.findDetailedById(bookId) : bookService.findById(bookId);
        return retrievedBook
                .map(book -> detailed ? BookMapper.toDetailedDTO(book) : BookMapper.toDTO(book))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(Book.class, bookId));
    }

    @PostMapping
    public ResponseEntity<BookDTO> addBook(@RequestBody @Valid NewBookDTO newBook) {
        var bookToSave = BookMapper.toDomain(newBook);
        var savedBook = bookService.save(newBook.getAuthorId(), bookToSave);

        return ResponseEntity.ok(BookMapper.toDetailedDTO(savedBook));
    }

    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<List<BookReviewDTO>> getBookReviews(@PathVariable Long bookId) {
        var bookReviews = bookService.findReviews(bookId).stream()
                .map(ReviewMapper::toDTO)
                .toList();
        return ResponseEntity.ok(bookReviews);
    }

    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<BookReviewDTO> addBookReview(@PathVariable Long bookId,
                                                        @RequestBody @Valid NewBookReviewDTO newReview) {
        var reviewToSave = ReviewMapper.toDomain(newReview);
        var savedReview = bookService.addReview(bookId, reviewToSave);

        return ResponseEntity.ok(ReviewMapper.toDTO(savedReview));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<DetailedBookDTO> updateBook(@PathVariable Long bookId,
                                                      @RequestBody @Valid UpdateBookDTO updateBook) {
        var bookUpdate = BookMapper.toDomain(updateBook);
        var updatedBook = bookService.update(bookId, bookUpdate);

        return ResponseEntity.ok(BookMapper.toDetailedDTO(updatedBook));
    }

    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable Long bookId) {
        bookService.delete(bookId);
    }

}
