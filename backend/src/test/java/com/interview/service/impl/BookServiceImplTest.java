package com.interview.service.impl;

import com.interview.dto.BookDto;
import com.interview.dto.PaginatedBooksDto;
import com.interview.entity.Author;
import com.interview.entity.Book;
import com.interview.repository.BookRepository;
import com.interview.testutil.CommonTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        Author author = new Author(
                CommonTestConstants.ID_1,
                CommonTestConstants.FIRST_NAME_1,
                CommonTestConstants.LAST_NAME_1,
                CommonTestConstants.PHOTO_URL_1,
                new ArrayList<>());

        book = new Book(
                CommonTestConstants.ID_1,
                CommonTestConstants.NAME_1,
                author,
                CommonTestConstants.PUBLICATION_YEAR);

        bookDto = new BookDto(book);
    }

    @Test
    void testGetAllWithKeyword() {
        String keyword = "Java";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(Arrays.asList(book));

        when(bookRepository.findAllByKeyword(keyword.toUpperCase(), pageable)).thenReturn(page);

        PaginatedBooksDto result = bookService.getAll(keyword, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        assertEquals(1, result.getBooks().size());
        assertEquals(book.getId(), result.getBooks().get(0).getId());
        verify(bookRepository, times(1)).findAllByKeyword(keyword.toUpperCase(), pageable);
    }

    @Test
    void testGetAllWithoutKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(Arrays.asList(book));

        when(bookRepository.findAllByKeyword(null, pageable)).thenReturn(page);

        PaginatedBooksDto result = bookService.getAll(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        assertEquals(1, result.getBooks().size());
        assertEquals(book.getId(), result.getBooks().get(0).getId());
        verify(bookRepository, times(1)).findAllByKeyword(null, pageable);
    }

    @Test
    void testFindByIdFound() {
        when(bookRepository.findById(CommonTestConstants.ID_1)).thenReturn(Optional.of(book));

        BookDto result = bookService.findById(CommonTestConstants.ID_1);

        assertNotNull(result);
        assertEquals(CommonTestConstants.ID_1, result.getId());
        verify(bookRepository, times(1)).findById(CommonTestConstants.ID_1);
    }

    @Test
    void testFindByIdNotFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        BookDto result = bookService.findById(2L);

        assertNull(result);
        verify(bookRepository, times(1)).findById(2L);
    }

    @Test
    void testSave() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto result = bookService.save(bookDto);

        assertNotNull(result);
        assertEquals(CommonTestConstants.ID_1, result.getId());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testDelete() {
        doNothing().when(bookRepository).deleteById(CommonTestConstants.ID_1);

        assertDoesNotThrow(() -> bookService.delete(CommonTestConstants.ID_1));

        verify(bookRepository, times(1)).deleteById(CommonTestConstants.ID_1);
    }
}
