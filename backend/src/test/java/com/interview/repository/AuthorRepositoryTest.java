package com.interview.repository;

import com.interview.entity.Author;
import com.interview.testutil.CommonTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testFindAllByKeyword() {
        Author author1 = new Author();
        author1.setFirstName(CommonTestConstants.FIRST_NAME_1);
        author1.setLastName(CommonTestConstants.LAST_NAME_1);
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setFirstName(CommonTestConstants.FIRST_NAME_2);
        author2.setLastName(CommonTestConstants.LAST_NAME_2);
        authorRepository.save(author2);

        String keyword = "1";

        Page<Author> result = authorRepository.findAllByKeyword(keyword, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(a -> a.getFirstName().equals(CommonTestConstants.FIRST_NAME_1)));
    }

    @Test
    void testFindAllByNonExistentKeyword() {
        Author author1 = new Author();
        author1.setFirstName(CommonTestConstants.FIRST_NAME_1);
        author1.setLastName(CommonTestConstants.LAST_NAME_1);
        authorRepository.save(author1);

        String keyword = "NonExistent";

        Page<Author> result = authorRepository.findAllByKeyword(keyword, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindAllByNullKeyword() {
        Author author1 = new Author();
        author1.setFirstName(CommonTestConstants.FIRST_NAME_1);
        author1.setLastName(CommonTestConstants.LAST_NAME_1);
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setFirstName(CommonTestConstants.FIRST_NAME_2);
        author2.setLastName(CommonTestConstants.LAST_NAME_2);
        authorRepository.save(author2);

        String keyword = null;

        Page<Author> result = authorRepository.findAllByKeyword(keyword, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }
}
