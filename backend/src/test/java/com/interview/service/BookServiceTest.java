package com.interview.service;

import com.interview.exception.BookStoreExceptions.BookNotFoundException;
import com.interview.exception.BookStoreExceptions.DuplicateIsbnException;
import com.interview.exception.BookStoreExceptions.OptimisticLockConflictException;
import com.interview.mapper.BookMapper;
import com.interview.repository.BookRepository;
import com.interview.repository.model.Book;
import com.interview.repository.model.Genre;
import com.interview.resource.model.BookDto.BookResponse;
import com.interview.resource.model.BookDto.CreateRequest;
import com.interview.resource.model.BookDto.UpdateRequest;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    private CreateRequest sampleCreateRequest;
    private UpdateRequest sampleUpdateRequest;
    private Book sampleBook;
    private BookResponse sampleResponse;
    private UUID sampleId;

    @BeforeEach
    void setup() {
        sampleId = UUID.randomUUID();
        sampleCreateRequest = new CreateRequest(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                Genre.TECHNOLOGY,
                BigDecimal.valueOf(35.99),
                LocalDate.of(2008, 8, 1),
                "A classic book"
        );

        sampleUpdateRequest = new UpdateRequest(
                "Clean Code 2nd Edition",
                "Robert C. Martin",
                "978-0132350884",
                Genre.TECHNOLOGY,
                BigDecimal.valueOf(40.00),
                LocalDate.of(2021, 1, 1),
                "Updated edition",
                1L
        );

        sampleBook = new Book();
        sampleBook.setId(sampleId);
        sampleBook.setTitle(sampleCreateRequest.title());
        sampleBook.setAuthor(sampleCreateRequest.author());
        sampleBook.setIsbn(sampleCreateRequest.isbn());
        sampleBook.setGenre(sampleCreateRequest.genre());
        sampleBook.setPrice(sampleCreateRequest.price());
        sampleBook.setPublishedAt(sampleCreateRequest.publishedAt());
        sampleBook.setDescription(sampleCreateRequest.description());
        sampleBook.setVersion(1L);
        sampleBook.setCreatedAt(Instant.now());
        sampleBook.setUpdatedAt(Instant.now());

        sampleResponse = new BookResponse(
                sampleId,
                sampleBook.getTitle(),
                sampleBook.getAuthor(),
                sampleBook.getIsbn(),
                sampleBook.getGenre(),
                sampleBook.getPrice(),
                sampleBook.getPublishedAt(),
                sampleBook.getDescription(),
                sampleBook.getVersion(),
                sampleBook.getCreatedAt(),
                sampleBook.getUpdatedAt()
        );
    }

    @Test
    void createBook_success() {
        when(bookRepository.existsByIsbn(sampleCreateRequest.isbn())).thenReturn(false);
        when(bookMapper.toEntity(sampleCreateRequest)).thenReturn(sampleBook);
        when(bookRepository.save(sampleBook)).thenReturn(sampleBook);
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        BookResponse response = bookService.createBook(sampleCreateRequest);

        assertThat(response).isEqualTo(sampleResponse);
        verify(bookRepository).save(sampleBook);
    }

    @Test
    void createBook_duplicateIsbn_throws() {
        when(bookRepository.existsByIsbn(sampleCreateRequest.isbn())).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(sampleCreateRequest))
                .isInstanceOf(DuplicateIsbnException.class)
                .hasMessageContaining(sampleCreateRequest.isbn());

        verify(bookRepository, never()).save(any());
    }

    @Test
    void getBook_success() {
        when(bookRepository.findById(sampleId)).thenReturn(Optional.of(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        BookResponse response = bookService.getBook(sampleId);

        assertThat(response).isEqualTo(sampleResponse);
    }

    @Test
    void getBook_notFound_throws() {
        when(bookRepository.findById(sampleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBook(sampleId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining(sampleId.toString());
    }

    @Test
    void listBooks_success() {
        Page<Book> page = new PageImpl<>(List.of(sampleBook));
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.search(null, null, null, null, pageable)).thenReturn(page);
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        Page<BookResponse> result = bookService.listBooks(null, null, null, null, pageable);

        assertThat(result.getContent()).containsExactly(sampleResponse);
    }

    @Test
    void updateBook_success() {
        when(bookRepository.findById(sampleId)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.existsByIsbnAndIdNot(sampleUpdateRequest.isbn(), sampleId)).thenReturn(false);
        doNothing().when(bookMapper)
                .updateEntity(sampleUpdateRequest, sampleBook);
        when(bookRepository.saveAndFlush(sampleBook)).thenReturn(sampleBook);
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        BookResponse response = bookService.updateBook(sampleId, sampleUpdateRequest);

        assertThat(response).isEqualTo(sampleResponse);
    }

    @Test
    void updateBook_notFound_throws() {
        when(bookRepository.findById(sampleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(sampleId, sampleUpdateRequest))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void updateBook_duplicateIsbn_throws() {
        when(bookRepository.findById(sampleId)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.existsByIsbnAndIdNot(sampleUpdateRequest.isbn(), sampleId)).thenReturn(true);

        assertThatThrownBy(() -> bookService.updateBook(sampleId, sampleUpdateRequest))
                .isInstanceOf(DuplicateIsbnException.class);
    }

    @Test
    void updateBook_optimisticLock_throws() {
        when(bookRepository.findById(sampleId)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.existsByIsbnAndIdNot(sampleUpdateRequest.isbn(), sampleId)).thenReturn(false);
        doThrow(OptimisticLockException.class).when(bookMapper)
                .updateEntity(sampleUpdateRequest, sampleBook);

        assertThatThrownBy(() -> bookService.updateBook(sampleId, sampleUpdateRequest))
                .isInstanceOf(OptimisticLockConflictException.class);
    }

    @Test
    void deleteBook_success() {
        when(bookRepository.existsById(sampleId)).thenReturn(true);

        bookService.deleteBook(sampleId);

        verify(bookRepository).deleteById(sampleId);
    }

    @Test
    void deleteBook_notFound_throws() {
        when(bookRepository.existsById(sampleId)).thenReturn(false);

        assertThatThrownBy(() -> bookService.deleteBook(sampleId))
                .isInstanceOf(BookNotFoundException.class);
    }
}