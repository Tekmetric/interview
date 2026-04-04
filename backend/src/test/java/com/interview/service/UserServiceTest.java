package com.interview.service;

import com.interview.model.User;
import com.interview.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @Test
    void findUserById_whenFound_returnsUser() {
        User user = new User("Alice", "alice@example.com",28);
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User actual = userService.findUserById(1L);

        assertEquals(user.getName(), actual.getName());
        assertEquals(user.getEmail(), actual.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void findUserById_whenNotFound_throwsException() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(100L));

        assertTrue(exception.getMessage().contains("User not found with id: 100"));
        verify(userRepository).findById(100L);
    }

    @Test
    void saveUser_persistsUser() {
        User user = new User("Bob", "bob@example.com", 30);

        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.saveUser(user);

        assertEquals(user, saved);
        verify(userRepository).save(user);
    }
}
