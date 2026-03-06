package com.interview.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Welcome", description = "Basic welcome endpoint")
public class WelcomeResource {

    @GetMapping("/api/welcome")
    @Operation(
            summary = "Welcome endpoint",
            description = "Returns a welcome message for authenticated users.",
            tags = {"Welcome"},
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    public String index() {

        return "Welcome to the interview project!";
    }
}
