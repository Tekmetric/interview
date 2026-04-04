package com.interview.controller;

import com.interview.model.User;
import com.interview.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    @Test
    void getUserById_whenFound_returnsOk() {
        User user = new User("Jane", "jane@example.com", 28);
        user.setId(5L);

        when(userService.findUserById(5L)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(5L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void createUser_returnsCreated() {
        User user = new User("Jane", "jane@example.com", 28);

        when(userService.saveUser(user)).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService).saveUser(user);
    }
}
