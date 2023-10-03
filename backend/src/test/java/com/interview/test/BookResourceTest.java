package com.interview.test;


import com.interview.model.Book;
import com.interview.repository.BookRepository;
import com.interview.resource.BookResource;
import com.interview.util.BookData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookResourceTest {

    @InjectMocks
    BookResource bookResource;

    @Mock
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(BookData.getBooks());

        assertEquals(5, bookResource.getAllBooks().size());
    }

    @Test
    public void testGetBookById() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(BookData.getBooks().get(0)));

        Book book = bookResource.getBookById(bookId);
        assertNotNull(book);
        assertEquals("Book 1", book.getTitle());
    }

    @Test
    public void testAddBook() {
        Book newBook = new Book(0, "New Book", "New Author");
        when(bookRepository.save(newBook)).thenReturn(new Book(6L, "New Book", "New Author"));

        Book savedBook = bookResource.addBook(newBook);
        assertNotNull(savedBook);
        assertEquals(Long.valueOf(6), savedBook.getId());
    }

    @Test
    public void testUpdateBook() {
        Long bookId = 1L;
        Book updatedBook = new Book(bookId, "Updated Book", "Updated Author");

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        Book result = bookResource.updateBook(bookId, updatedBook);
        assertNotNull(result);
        assertEquals("Updated Book", result.getTitle());
    }

    @Test
    public void testDeleteBook() {
        Long bookId = 1L;
        doNothing().when(bookRepository).deleteById(bookId);

        bookResource.deleteBook(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }
}
