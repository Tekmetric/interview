package com.interview.resource;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class WelcomeResource {

    @RequestMapping("/api/welcome")
    public String index() {

        return "Welcome to the interview project!";
    }
}