package com.interview.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class WelcomeResource {

    @RequestMapping("/api/welcome")
    public String index() {

        return "Welcome to the interview project!";
    }
}