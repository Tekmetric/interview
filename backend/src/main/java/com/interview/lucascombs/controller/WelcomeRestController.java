package com.interview.lucascombs.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeRestController {

    @RequestMapping("/api/welcome")
    public String index() {

        return "Welcome to the interview project!";
    }
}
