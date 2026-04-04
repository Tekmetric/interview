package com.interview.repository;

import com.interview.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql({
  "/data/cleanup.sql",
  "/data/data.sql"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllUsers_shouldReturnAllUsers() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(3);  // Based on data.sql
    }

    @Test
    void findUserById_shouldReturnUser_whenExists() {
        Optional<User> user = userRepository.findById(1L);
        assertThat(user).isPresent();
        assertThat(user.get().getName()).isEqualTo("Alice Johnson");
    }

    @Test
    void findUserById_shouldReturnEmpty_whenNotExists() {
        Optional<User> user = userRepository.findById(999L);
        assertThat(user).isEmpty();
    }

    @Test
    void saveUser_shouldPersistUser() {
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("test@example.com");
        newUser.setAge(25);

        User saved = userRepository.save(newUser);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test User");
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        userRepository.deleteById(1L);
        Optional<User> deleted = userRepository.findById(1L);
        assertThat(deleted).isEmpty();
    }
}