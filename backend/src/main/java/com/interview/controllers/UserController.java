package com.interview.controllers;

import com.interview.models.user.User;
import com.interview.models.user.dto.CreateUserRequest;
import com.interview.models.user.dto.UpdateUserRequest;
import com.interview.models.user.dto.UserResponse;
import com.interview.services.AuthService;
import com.interview.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) throws Exception {
        UserResponse response = userService.createUser(createUserRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get user by ID (requires Basic Auth and must match authenticated user)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@RequestHeader(name = "Authorization") String authorization,
                                                    @PathVariable Long id) {
        Optional<User> authUser = authService.authenticate(authorization);
        if (authUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        if (!authUser.get().getId().equals(id)) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        Optional<UserResponse> user = userService.getUserById(id);
        return user.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    // Update user (partial updates allowed; do not validate full object) — requires Basic Auth and must match authenticated user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@RequestHeader(name = "Authorization") String authorization,
                                                   @PathVariable Long id,
                                                   @RequestBody UpdateUserRequest updateUserRequest) {
        Optional<User> authUser = authService.authenticate(authorization);
        if (authUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        if (!authUser.get().getId().equals(id)) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        Optional<UserResponse> updatedUser = userService.updateUser(id, updateUserRequest);
        return updatedUser.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                          .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

}
