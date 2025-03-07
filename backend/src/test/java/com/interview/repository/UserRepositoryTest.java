package com.interview.repository;

import com.interview.entity.User;
import com.interview.testutil.CommonTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFirstName(CommonTestConstants.FIRST_NAME_1);
        user.setLastName(CommonTestConstants.LAST_NAME_1);
        user.setEmail(CommonTestConstants.EMAIL_1);
        user.setPassword(CommonTestConstants.PASSWORD);
        userRepository.save(user);
    }

    @Test
    void testFindByEmail() {
        Optional<User> result = userRepository.findByEmail(CommonTestConstants.EMAIL_1);

        assertTrue(result.isPresent());
        assertEquals(CommonTestConstants.EMAIL_1, result.get().getEmail());
        assertEquals(CommonTestConstants.FIRST_NAME_1, result.get().getFirstName());
        assertEquals(CommonTestConstants.LAST_NAME_1, result.get().getLastName());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }
}
