package com.interview.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeResource {

    private final String welcomeMessage;

    public WelcomeResource(@Value("${app.welcome.message}") String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    @RequestMapping("/api/welcome")
    public String index() {

        return welcomeMessage;
    }
}