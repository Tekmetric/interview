package com.interview.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class HealthCheckController {
    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void health() {}
}
