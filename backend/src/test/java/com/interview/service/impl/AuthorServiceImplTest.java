package com.interview.service.impl;

import com.interview.dto.AuthorDto;
import com.interview.dto.BookDto;
import com.interview.dto.PaginatedAuthorsDto;
import com.interview.entity.Author;
import com.interview.entity.Book;
import com.interview.repository.AuthorRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;
    private AuthorDto authorDto;
    private Book book;

    @BeforeEach
    void setUp() {
        author = new Author(
                CommonTestConstants.ID_1,
                CommonTestConstants.FIRST_NAME_1,
                CommonTestConstants.LAST_NAME_1,
                CommonTestConstants.PHOTO_URL_1,
                new ArrayList<>());

        authorDto = new AuthorDto(author);

        book = new Book(
                CommonTestConstants.ID_1,
                CommonTestConstants.NAME_1,
                author,
                CommonTestConstants.PUBLICATION_YEAR);
    }

    @Test
    void testGetAllWithKeyword() {
        String keyword = "first";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Author> page = new PageImpl<>(Arrays.asList(author));

        when(authorRepository.findAllByKeyword(keyword.toUpperCase(), pageable)).thenReturn(page);

        PaginatedAuthorsDto result = authorService.getAll(keyword, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        assertEquals(1, result.getAuthors().size());
        assertEquals(author.getId(), result.getAuthors().get(0).getId());
        verify(authorRepository, times(1)).findAllByKeyword(keyword.toUpperCase(), pageable);
    }

    @Test
    void testGetAllWithoutKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Author> page = new PageImpl<>(Arrays.asList(author));

        when(authorRepository.findAll(pageable)).thenReturn(page);

        PaginatedAuthorsDto result = authorService.getAll(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        assertEquals(1, result.getAuthors().size());
        assertEquals(author.getId(), result.getAuthors().get(0).getId());
        verify(authorRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindByIdFound() {
        when(authorRepository.findById(CommonTestConstants.ID_1)).thenReturn(Optional.of(author));

        AuthorDto result = authorService.findById(1L);

        assertNotNull(result);
        assertEquals(CommonTestConstants.ID_1, result.getId());
        assertEquals(CommonTestConstants.FIRST_NAME_1, result.getFirstName());
        verify(authorRepository, times(1)).findById(CommonTestConstants.ID_1);
    }

    @Test
    void testFindByIdNotFound() {
        when(authorRepository.findById(2L)).thenReturn(Optional.empty());

        AuthorDto result = authorService.findById(2L);

        assertNull(result);
        verify(authorRepository, times(1)).findById(2L);
    }

    @Test
    void testFindBooksOfAuthorById() {
        when(bookRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(book));

        List<BookDto> result = authorService.findBooksOfAuthorById(CommonTestConstants.ID_1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CommonTestConstants.NAME_1, result.get(0).getName());
        verify(bookRepository, times(1)).findByAuthorId(CommonTestConstants.ID_1);
    }

    @Test
    void testSave() {
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorDto result = authorService.save(authorDto);

        assertNotNull(result);
        assertEquals(CommonTestConstants.ID_1, result.getId());
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testUpdateFound() {
        when(authorRepository.findById(CommonTestConstants.ID_1)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorDto result = authorService.update(authorDto);

        assertNotNull(result);
        assertEquals(CommonTestConstants.ID_1, result.getId());
        verify(authorRepository, times(1)).findById(CommonTestConstants.ID_1);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testUpdateNotFound() {
        when(authorRepository.findById(2L)).thenReturn(Optional.empty());

        AuthorDto result = authorService.update(new AuthorDto(2L, "Jane","Doe", null));

        assertNull(result);
        verify(authorRepository, times(1)).findById(2L);
        verify(authorRepository, times(0)).save(any(Author.class));
    }

    @Test
    void testDelete() {
        doNothing().when(authorRepository).deleteById(CommonTestConstants.ID_1);

        assertDoesNotThrow(() -> authorService.delete(CommonTestConstants.ID_1));

        verify(authorRepository, times(1)).deleteById(CommonTestConstants.ID_1);
    }
}
