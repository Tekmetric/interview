package com.interview.repository;

import com.interview.entity.Author;
import com.interview.entity.Book;
import com.interview.testutil.CommonTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Pageable pageable;

    private Author author1;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);  // Sayfa başı 10 öğe

        author1 = new Author();
        author1.setFirstName(CommonTestConstants.FIRST_NAME_1);
        author1.setLastName(CommonTestConstants.LAST_NAME_1);
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setFirstName(CommonTestConstants.FIRST_NAME_2);
        author2.setLastName(CommonTestConstants.LAST_NAME_2);
        authorRepository.save(author2);

        Book book1 = new Book();
        book1.setName(CommonTestConstants.NAME_1);
        book1.setAuthor(author1);
        book1.setPublicationYear(CommonTestConstants.PUBLICATION_YEAR);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setName(CommonTestConstants.NAME_2);
        book2.setAuthor(author2);
        book2.setPublicationYear(CommonTestConstants.PUBLICATION_YEAR);
        bookRepository.save(book2);
    }

    @Test
    void testFindAllByKeyword() {
        String keyword = "name";

        Page<Book> result = bookRepository.findAllByKeyword(keyword.toUpperCase(), pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(b -> b.getName().equals(CommonTestConstants.NAME_1)));
        assertTrue(result.getContent().stream().anyMatch(b -> b.getName().equals(CommonTestConstants.NAME_2)));
    }

    @Test
    void testFindAllByKeywordWithNonExistentKeyword() {
        String keyword = "NonExistent";

        Page<Book> result = bookRepository.findAllByKeyword(keyword, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindAllByKeywordWithNullKeyword() {
        String keyword = null;

        Page<Book> result = bookRepository.findAllByKeyword(keyword, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testFindAllByKeywordWithAuthorFirstName() {
        String keyword = CommonTestConstants.FIRST_NAME_1;

        Page<Book> result = bookRepository.findAllByKeyword(keyword.toUpperCase(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(b -> b.getName().equals(CommonTestConstants.NAME_1)));
    }

    @Test
    void testFindByAuthorId() {
        List<Book> result = bookRepository.findByAuthorId(author1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CommonTestConstants.NAME_1, result.get(0).getName());
    }

    @Test
    void testFindByAuthorIdWithNoBooks() {
        List<Book> result = bookRepository.findByAuthorId(-1L);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindByAuthorIdWithMultipleBooks() {
        Book book3 = new Book();
        book3.setName("Book Three");
        book3.setAuthor(author1);
        bookRepository.save(book3);

        List<Book> result = bookRepository.findByAuthorId(author1.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(b -> b.getName().equals(CommonTestConstants.NAME_1)));
        assertTrue(result.stream().anyMatch(b -> b.getName().equals("Book Three")));
    }
}
