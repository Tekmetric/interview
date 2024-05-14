package com.interview.bookstore.api;

import com.interview.bookstore.api.dto.AuthorDTO;
import com.interview.bookstore.api.dto.BookDTO;
import com.interview.bookstore.api.dto.BookReviewDTO;
import com.interview.bookstore.api.dto.DetailedBookDTO;
import com.interview.bookstore.api.dto.NewBookDTO;
import com.interview.bookstore.api.dto.NewBookReviewDTO;
import com.interview.bookstore.api.dto.UpdateBookDTO;
import com.interview.bookstore.domain.Book;
import com.interview.bookstore.domain.BookReview;
import com.interview.bookstore.persistence.BookRepository;
import com.interview.bookstore.persistence.BookReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class BookControllerTest {

    private static final Long AUTHOR_ID = 1L;
    private static final Long BOOK_ONE_ID = 1L;
    private static final Long BOOK_TWO_ID = 2L;
    private static final Long BOOK_THREE_ID = 3L;
    private static final String BOOK_ONE_ISBN = "978-0307474278";
    private static final String BOOK_THREE_ISBN = "978-0307743657";
    private static final String BOOK_ONE_TITLE = "Book: Vol. I";
    private static final String BOOK_THREE_TITLE = "Book: Vol. III";

    @Autowired private BookController bookController;
    @Autowired private BookRepository bookRepository;
    @Autowired private BookReviewRepository bookReviewRepository;

    @Test
    void retrieveAllBooks() {
        ResponseEntity<List<BookDTO>> allBooksResponse = bookController.getAllBooks();

        assertThat(allBooksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var retrievedBooks = allBooksResponse.getBody();
        assertThat(retrievedBooks)
                .hasSize(3)
                .extracting("id", Long.class)
                    .contains(BOOK_ONE_ID, BOOK_TWO_ID, BOOK_THREE_ID);
        assertThat(retrievedBooks)
                .extracting("author", AuthorDTO.class)
                .isNotNull();
    }

    @Test
    void retrieveSingleBookWithoutDetails() {
        ResponseEntity<?> bookOneResponse =  bookController.getBook(BOOK_ONE_ID, false);

        assertThat(bookOneResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookOneResponse.getBody()).isInstanceOf(BookDTO.class);
        BookDTO retrievedBook = (BookDTO) bookOneResponse.getBody();
        assertThat(retrievedBook.getId()).isEqualTo(BOOK_ONE_ID);
        assertThat(retrievedBook.getTitle()).isEqualTo(BOOK_ONE_TITLE);
    }

    @Test
    void retrieveDetailedBookWithReviews() {
        ResponseEntity<?> detailedBookResponse =  bookController.getBook(BOOK_ONE_ID, true);

        assertThat(detailedBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detailedBookResponse.getBody()).isInstanceOf(DetailedBookDTO.class);

        DetailedBookDTO retrievedBook = (DetailedBookDTO) detailedBookResponse.getBody();
        assertThat(retrievedBook.getId()).isEqualTo(BOOK_ONE_ID);
        assertThat(retrievedBook.getIsbn()).isEqualTo(BOOK_ONE_ISBN);
        assertThat(retrievedBook.getTitle()).isEqualTo(BOOK_ONE_TITLE);
        assertThat(retrievedBook.getAuthor()).isNotNull();
        assertThat(retrievedBook.getReviews())
                .extracting("id", Long.class)
                    .contains(1L, 2L, 3L);
    }

    @Test
    void retrieveDetailedBookThatHasNoReviews() {
        ResponseEntity<?> detailedBookResponse =  bookController.getBook(BOOK_THREE_ID, true);

        assertThat(detailedBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detailedBookResponse.getBody()).isInstanceOf(DetailedBookDTO.class);

        DetailedBookDTO retrievedBook = (DetailedBookDTO) detailedBookResponse.getBody();
        assertThat(retrievedBook.getId()).isEqualTo(BOOK_THREE_ID);
        assertThat(retrievedBook.getIsbn()).isEqualTo(BOOK_THREE_ISBN);
        assertThat(retrievedBook.getTitle()).isEqualTo(BOOK_THREE_TITLE);
        assertThat(retrievedBook.getAuthor()).isNotNull();
        assertThat(retrievedBook.getReviews()).isEmpty();
    }

    @Test
    void addNewBook() {
        NewBookDTO newBookDTO = new NewBookDTO();
        newBookDTO.setAuthorId(AUTHOR_ID);
        newBookDTO.setTitle("Book: Vol. IV");
        newBookDTO.setIsbn("978-0307474279");
        newBookDTO.setPrice(20d);
        newBookDTO.setPublicationDate(LocalDate.now());
        newBookDTO.setPageCount(515);

        ResponseEntity<BookDTO> addBookResponse = bookController.addBook(newBookDTO);

        assertThat(addBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addBookResponse.getBody()).isNotNull();

        BookDTO savedBook = addBookResponse.getBody();
        assertThat(savedBook.getId()).isNotNull();

        Optional<Book> retrievedDatabaseBook = bookRepository.findById(savedBook.getId());
        assertThat(retrievedDatabaseBook).isPresent();
        var databaseBook = retrievedDatabaseBook.get();
        assertThat(databaseBook.getTitle()).isEqualTo(newBookDTO.getTitle());
        assertThat(databaseBook.getPrice()).isEqualTo(newBookDTO.getPrice());
        assertThat(databaseBook.getAuthor()).isNotNull();

        var databaseBookDetails = databaseBook.getBookDetails();
        assertThat(databaseBookDetails.getDescription()).isNull();
        assertThat(databaseBookDetails.getIsbn()).isEqualTo(newBookDTO.getIsbn());
        assertThat(databaseBookDetails.getPublicationDate()).isEqualTo(newBookDTO.getPublicationDate());
        assertThat(databaseBookDetails.getPageCount()).isEqualTo(newBookDTO.getPageCount());
    }

    @Test
    void retrievePopulatedBookReviewList() {
        ResponseEntity<List<BookReviewDTO>> bookReviewsResponse =  bookController.getBookReviews(BOOK_ONE_ID);

        assertThat(bookReviewsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var bookReviews = bookReviewsResponse.getBody();
        assertThat(bookReviews)
                .hasSize(3)
                .extracting("id", Long.class)
                    .contains(1L, 2L, 3L);
    }

    @Test
    void retrieveEmptyBookReviewList() {
        ResponseEntity<List<BookReviewDTO>> bookReviewsResponse = bookController.getBookReviews(BOOK_THREE_ID);

        assertThat(bookReviewsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var bookReviews = bookReviewsResponse.getBody();
        assertThat(bookReviews).isEmpty();
    }

    @Test
    void addNewReviewForBook_withoutText() {
        NewBookReviewDTO newReviewDTO = new NewBookReviewDTO();
        newReviewDTO.setScore(4);

        ResponseEntity<BookReviewDTO> newBookReviewResponse = bookController.addBookReview(BOOK_ONE_ID, newReviewDTO);

        assertThat(newBookReviewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var savedBookReview = newBookReviewResponse.getBody();
        assertThat(savedBookReview.getId()).isNotNull();

        Optional<BookReview> retrievedDatabaseReview = bookReviewRepository.findById(savedBookReview.getId());
        assertThat(retrievedDatabaseReview).isPresent();
        var databaseReview = retrievedDatabaseReview.get();
        assertThat(databaseReview.getScore()).isEqualTo(newReviewDTO.getScore());
        assertThat(databaseReview.getText()).isNull();
    }

    @Test
    void addNewReviewForBook_withText() {
        NewBookReviewDTO newReviewDTO = new NewBookReviewDTO();
        newReviewDTO.setScore(4);
        newReviewDTO.setText("Good book.");

        ResponseEntity<BookReviewDTO> newBookReviewResponse = bookController.addBookReview(BOOK_ONE_ID, newReviewDTO);

        assertThat(newBookReviewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var savedBookReview = newBookReviewResponse.getBody();
        assertThat(savedBookReview.getId()).isNotNull();

        Optional<BookReview> retrievedDatabaseReview = bookReviewRepository.findById(savedBookReview.getId());
        assertThat(retrievedDatabaseReview).isPresent();
        var databaseReview = retrievedDatabaseReview.get();
        assertThat(databaseReview.getScore()).isEqualTo(newReviewDTO.getScore());
        assertThat(databaseReview.getText()).isEqualTo(newReviewDTO.getText());
    }

    @Test
    void updateBook() {
        UpdateBookDTO updateBookDTO = new UpdateBookDTO();
        updateBookDTO.setTitle(BOOK_ONE_TITLE);
        updateBookDTO.setDescription("Different book I description");
        updateBookDTO.setPrice(15d);
        updateBookDTO.setPageCount(600);

        bookController.updateBook(BOOK_ONE_ID, updateBookDTO);

        Optional<Book> retrievedDatabaseBook = bookRepository.findDetailedById(BOOK_ONE_ID);
        assertThat(retrievedDatabaseBook).isPresent();

        var databaseBook = retrievedDatabaseBook.get();
        assertThat(databaseBook.getTitle()).isEqualTo(BOOK_ONE_TITLE);
        assertThat(databaseBook.getPrice()).isEqualTo(updateBookDTO.getPrice());
        assertThat(databaseBook.getReviews()).isNotEmpty();
        var bookDetails = databaseBook.getBookDetails();
        assertThat(bookDetails.getDescription()).isEqualTo(updateBookDTO.getDescription());
        assertThat(bookDetails.getPageCount()).isEqualTo(updateBookDTO.getPageCount());
    }

    @Test
    void deleteBook() {
        bookController.deleteBook(BOOK_TWO_ID);

        Optional<Book> databaseBook = bookRepository.findById(BOOK_TWO_ID);
        assertThat(databaseBook).isNotPresent();

        List<BookReview> databaseBookReviews = bookReviewRepository.findAllByBookId(BOOK_TWO_ID);
        assertThat(databaseBookReviews).isEmpty();
    }

}
