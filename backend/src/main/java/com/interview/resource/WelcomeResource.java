package com.interview.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeResource {
    @GetMapping("/api/welcome")
    public String index() {

        return "Welcome to the interview project!";
    }
}