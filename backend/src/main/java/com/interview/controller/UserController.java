package com.interview.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.interview.dto.user.UserDto;
import com.interview.entity.User;
import com.interview.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Rest controller for the application.
 */
@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Get API version", description = "Returns the current API version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/version")
    public String version(){
        return "v1";
    }
    
    @Operation(summary = "Get welcome message", description = "Returns a welcome message")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome\n";
    }

    @Operation(summary = "Add a new user", description = "Creates a new user with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(value = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User aUser){
        return this.userService.addUser(aUser);
    }

    @Operation(summary = "Update an existing user", description = "Updates the details of an existing user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping(value = "/users")
    @ResponseStatus(HttpStatus.OK)
    public User modifyUser(@Valid @RequestBody User aUser){
        return this.userService.modifyUser(aUser);
    }


    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> listUsers()  {
    	List<User> users =  userService.listUsers();
        return users.stream().map(user ->
            modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable Long id)  {
        User user =  userService.getUser(id);
        return modelMapper.map(user, UserDto.class);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long id)  {
        userService.deleteUser(id);
    }
}
