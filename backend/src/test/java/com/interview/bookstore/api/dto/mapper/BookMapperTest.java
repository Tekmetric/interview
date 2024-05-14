package com.interview.bookstore.api.dto.mapper;

import com.interview.bookstore.api.dto.BookDTO;
import com.interview.bookstore.api.dto.DetailedBookDTO;
import com.interview.bookstore.api.dto.NewBookDTO;
import com.interview.bookstore.api.dto.UpdateBookDTO;
import com.interview.bookstore.domain.Author;
import com.interview.bookstore.domain.Book;
import com.interview.bookstore.domain.BookDetails;
import com.interview.bookstore.domain.BookReview;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class BookMapperTest {

    private static final Long BOOK_ID = 1L;
    private static final Long AUTHOR_ID = 10L;
    private static final String AUTHOR_FIRST_NAME = "John";
    private static final String AUTHOR_LAST_NAME = "Doe";
    private static final String TITLE = "Sample Title";
    private static final String DESCRIPTION = "Sample book description";
    private static final LocalDate PUBLICATION_DATE = LocalDate.now();
    private static final String ISBN = "978-0552169585";
    private static final Double PRICE = 15d;
    private static final Integer PAGE_COUNT = 444;
    private static final Integer REVIEW_SCORE = 5;
    private static final String REVIEW_TEXT = "Excellent book.";

    @Test
    void mapDomainBookToBookDTO() {
        Author author = new Author();
        setField(author, "id", AUTHOR_ID);
        author.setFirstName(AUTHOR_FIRST_NAME);
        author.setLastName(AUTHOR_LAST_NAME);

        Book bookEntity = new Book();
        setField(bookEntity, "id", BOOK_ID);
        bookEntity.setAuthor(author);
        bookEntity.setTitle(TITLE);
        bookEntity.setPrice(PRICE);

        BookDTO bookDTO = BookMapper.toDTO(bookEntity);

        assertThat(bookDTO.getId()).isEqualTo(BOOK_ID);
        assertThat(bookDTO.getTitle()).isEqualTo(TITLE);
        assertThat(bookDTO.getPrice()).isEqualTo(PRICE);
        assertThat(bookDTO.getAuthor()).isNotNull();
    }

    @Test
    void mapDomainBookToDetailedBookDTO() {
        Author author = new Author();
        setField(author, "id", AUTHOR_ID);
        author.setFirstName(AUTHOR_FIRST_NAME);
        author.setLastName(AUTHOR_LAST_NAME);

        Book bookEntity = new Book();
        setField(bookEntity, "id", BOOK_ID);
        bookEntity.setTitle(TITLE);
        bookEntity.setPrice(PRICE);

        BookDetails bookDetailsEntity = new BookDetails();
        setField(bookDetailsEntity, "id", BOOK_ID);
        bookDetailsEntity.setIsbn(ISBN);
        bookDetailsEntity.setDescription(DESCRIPTION);
        bookDetailsEntity.setPublicationDate(PUBLICATION_DATE);
        bookDetailsEntity.setPageCount(PAGE_COUNT);

        BookReview reviewEntity = new BookReview();
        reviewEntity.setScore(REVIEW_SCORE);
        reviewEntity.setText(REVIEW_TEXT);

        bookEntity.setAuthor(author);
        bookEntity.setBookDetails(bookDetailsEntity);
        bookEntity.setReviews(List.of(reviewEntity));

        DetailedBookDTO bookDTO = BookMapper.toDetailedDTO(bookEntity);

        assertThat(bookDTO.getId()).isEqualTo(BOOK_ID);
        assertThat(bookDTO.getTitle()).isEqualTo(TITLE);
        assertThat(bookDTO.getPrice()).isEqualTo(PRICE);
        assertThat(bookDTO.getIsbn()).isEqualTo(ISBN);
        assertThat(bookDTO.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(bookDTO.getPublicationDate()).isEqualTo(PUBLICATION_DATE);
        assertThat(bookDTO.getPageCount()).isEqualTo(PAGE_COUNT);
        assertThat(bookDTO.getAuthor()).isNotNull();
        assertThat(bookDTO.getReviews()).hasSize(1);
    }

    @Test
    void mapNewBookDTOToDomainBook() {
        NewBookDTO bookDTO = new NewBookDTO();
        bookDTO.setAuthorId(AUTHOR_ID);
        bookDTO.setTitle(TITLE);
        bookDTO.setPrice(PRICE);
        bookDTO.setIsbn(ISBN);
        bookDTO.setDescription(DESCRIPTION);
        bookDTO.setPublicationDate(PUBLICATION_DATE);
        bookDTO.setPageCount(PAGE_COUNT);

        Book bookEntity = BookMapper.toDomain(bookDTO);

        assertThat(bookEntity.getId()).isNull();
        assertThat(bookEntity.getTitle()).isEqualTo(TITLE);
        assertThat(bookEntity.getPrice()).isEqualTo(PRICE);
        assertThat(bookEntity.getAuthor()).isNull();
        assertThat(bookEntity.getReviews()).isEmpty();

        BookDetails bookDetailsEntity = bookEntity.getBookDetails();
        assertThat(bookDetailsEntity.getIsbn()).isEqualTo(ISBN);
        assertThat(bookDetailsEntity.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(bookDetailsEntity.getPublicationDate()).isEqualTo(PUBLICATION_DATE);
        assertThat(bookDetailsEntity.getPageCount()).isEqualTo(PAGE_COUNT);
    }

    @Test
    void mapUpdateBookDTOToDomainBook() {
        UpdateBookDTO bookDTO = new UpdateBookDTO();
        bookDTO.setTitle(TITLE);
        bookDTO.setDescription(DESCRIPTION);
        bookDTO.setPrice(PRICE);
        bookDTO.setPageCount(PAGE_COUNT);

        Book bookEntity = BookMapper.toDomain(bookDTO);

        assertThat(bookEntity.getTitle()).isEqualTo(TITLE);
        assertThat(bookEntity.getPrice()).isEqualTo(PRICE);

        BookDetails bookDetailsEntity = bookEntity.getBookDetails();
        assertThat(bookDetailsEntity.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(bookDetailsEntity.getPageCount()).isEqualTo(PAGE_COUNT);
    }

}
