package com.interview.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthResource {

    @RequestMapping("/api/health")
    public String index() {

        return "Application is Up!";
    }
}