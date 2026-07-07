package com.interview.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interview.service.UserService;
import com.interview.model.User;

@RestController
public class UserResource {

    @Autowired
    private UserService userService;

    @PostMapping("/api/users")
    public User addUser(User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/api/users")
    public List<User> getUser(@RequestParam(required = false) String name, @RequestParam(required = false) String organization) {
        return userService.searchUsers(name, organization);
    }

    @DeleteMapping("/api/users")
    public void deleteUser(@RequestParam String name, @RequestParam String organization) {
        userService.deleteUser(name, organization);
    }

    @GetMapping("/api/admins")
    public User getAdmin(@RequestParam String organization) {
        return userService.getOrganizationAdministrator(organization);
    }
}