package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.exception.BookStoreExceptions.InvalidSortParameterException;
import com.interview.repository.model.Genre;
import com.interview.resource.model.BookDto.BookResponse;
import com.interview.resource.model.BookDto.CreateRequest;
import com.interview.resource.model.BookDto.UpdateRequest;
import com.interview.service.BookService;
import com.interview.service.IdempotencyService;
import com.interview.service.IdempotencyService.CachedResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @InjectMocks
    private BookController controller;

    @Mock
    private BookService bookService;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private ObjectMapper objectMapper;

    private BookResponse sampleBook;
    private UUID sampleId;

    @BeforeEach
    void setup() {
        sampleId = UUID.randomUUID();
        sampleBook = new BookResponse(
                sampleId,
                "Sample",
                "Author",
                "123-456",
                Genre.FICTION,
                BigDecimal.TEN,
                LocalDate.of(2020, 1, 1),
                "Desc",
                1L,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void createBook_withCachedResponse_returnsCached() throws Exception {
        String idempotencyKey = "key-123";
        CachedResponse cached = mock(CachedResponse.class);
        when(idempotencyService.find(idempotencyKey)).thenReturn(Optional.of(cached));
        when(cached.bodyJson()).thenReturn("{}");
        when(objectMapper.readValue("{}", BookResponse.class)).thenReturn(sampleBook);

        CreateRequest request = new CreateRequest(
                "Sample",
                "Author",
                "123-456",
                Genre.FICTION,
                BigDecimal.TEN,
                LocalDate.of(2020, 1, 1),
                "Desc"
        );

        var response = controller.createBook(request, idempotencyKey);

        assertThat(response.getBody()).isEqualTo(sampleBook);
        verify(bookService, never()).createBook(any());
    }

    @Test
    void createBook_normalCall_callsService() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/books");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String idempotencyKey = null;
        CreateRequest createRequest = new CreateRequest(
                "Sample",
                "Author",
                "123-456",
                Genre.FICTION,
                BigDecimal.TEN,
                LocalDate.of(2020, 1, 1),
                "Desc"
        );

        when(bookService.createBook(createRequest)).thenReturn(sampleBook);

        var response = controller.createBook(createRequest, idempotencyKey);

        assertThat(response.getBody()).isEqualTo(sampleBook);
        verify(bookService).createBook(createRequest);
        verify(idempotencyService, never()).store(any(), anyInt(), any());
    }

    @Test
    void getBook_returnsServiceResult() {
        when(bookService.getBook(sampleId)).thenReturn(sampleBook);

        var response = controller.getBook(sampleId);

        assertThat(response.getBody()).isEqualTo(sampleBook);
    }

    @Test
    void listBooks_invalidSort_throwsException() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("invalidField"));
        assertThrows(InvalidSortParameterException.class,
                () -> controller.listBooks(null, null, null, null, pageable));
    }

    @Test
    void listBooks_validSort_callsService() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));
        Page<BookResponse> page = new PageImpl<>(java.util.List.of(sampleBook));
        when(bookService.listBooks(null, null, null, null, pageable)).thenReturn(page);

        var response = controller.listBooks(null, null, null, null, pageable);

        assertThat(response.getBody()
                .getContent()).containsExactly(sampleBook);
    }

    @Test
    void updateBook_withCachedResponse_returnsCached() throws Exception {
        String idempotencyKey = "key-123";
        CachedResponse cached = mock(CachedResponse.class);
        when(idempotencyService.find(idempotencyKey)).thenReturn(Optional.of(cached));
        when(cached.bodyJson()).thenReturn("{}");
        when(objectMapper.readValue("{}", BookResponse.class)).thenReturn(sampleBook);

        UpdateRequest request = new UpdateRequest(
                "Sample",
                "Author",
                "123-456",
                Genre.FICTION,
                BigDecimal.TEN,
                LocalDate.of(2020, 1, 1),
                "Desc",
                1L
        );

        var response = controller.updateBook(sampleId, request, idempotencyKey);

        assertThat(response.getBody()).isEqualTo(sampleBook);
        verify(bookService, never()).updateBook(any(), any());
    }

    @Test
    void deleteBook_callsService() {
        controller.deleteBook(sampleId);
        verify(bookService).deleteBook(sampleId);
    }
}