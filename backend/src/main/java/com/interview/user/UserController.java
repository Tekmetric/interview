package com.interview.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;


    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDto createUser(@Valid @RequestBody User newUser) {
        User u = userService.save(newUser);
        return userMapper.userToUserDto(u);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto userById(@PathVariable Long id) {
        User u = userService.findById(id);
        return userMapper.userToUserDto(u);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> all() {
        return userService.findAll().stream().map(u -> userMapper.userToUserDto(u)).toList();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @PutMapping(
        value = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDto editUser(@PathVariable Long id, @Valid @RequestBody UserDto editedUser) {
        User u = userService.findById(id);
        u = userMapper.updateFromDto(editedUser, u);
        return userMapper.userToUserDto(userService.save(u));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundHandler(UserNotFoundException e) {
        return e.getMessage();
    }

}
