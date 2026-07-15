package com.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/welcome")
@Tag(name = "Welcome", description = "Health check endpoint")
public class WelcomeController {

  @GetMapping
  @Operation(summary = "Welcome message",
      description = "Returns a simple welcome string")
  public String index() {
    return "Welcome to the interview project!";
  }
}
