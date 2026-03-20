package com.interview.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DefaultExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    private static class TestController {
        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Item not found");
        }

        @GetMapping("/test/exists")
        public void throwExists() {
            throw new ResourceAlreadyExistsException("Item already exists");
        }

        @GetMapping("/test/error")
        public void throwRuntime() {
            throw new RuntimeException("Unexpected boom");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new DefaultExceptionHandler())
                .build();
    }

    @Test
    void whenResourceNotFound_thenReturns404() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void whenResourceAlreadyExists_thenReturns409() throws Exception {
        mockMvc.perform(get("/test/exists"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Item already exists"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void whenGeneralException_thenReturns500() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }
}