package com.interview.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.models.user.Gender;
import com.interview.models.user.dto.CreateUserRequest;
import com.interview.models.user.dto.UserResponse;
import com.interview.services.AuthService;
import com.interview.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/user with invalid email returns 400 and ErrorResponse.details.email")
    void createUser_invalidEmail_returns400() throws Exception {
        CreateUserRequest bad = new CreateUserRequest(
                "John",
                null,
                "Doe",
                "johnny",
                "SecurePass123$",
                LocalDate.of(1990,1,15),
                "123-45-6789",
                Gender.MALE,
                "john.doeexample.com", // invalid
                "555-010-8888"
        );

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.details.email", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/user success returns 201 and body")
    void createUser_success_returns201() throws Exception {
        CreateUserRequest req = new CreateUserRequest(
                "Jane",
                null,
                "Doe",
                "janedoe",
                "SecurePass123$",
                LocalDate.of(1992,5,10),
                "123456789",
                Gender.FEMALE,
                "jane.doe@example.com",
                "5550101234"
        );

        UserResponse resp = new UserResponse(2L, "Jane", null, "Doe", "janedoe",
                req.dateOfBirth(), 30, Gender.FEMALE, req.email(), req.phoneNumber(), null, null);

        Mockito.when(userService.createUser(any(CreateUserRequest.class))).thenReturn(resp);

        // Because password is marked WRITE_ONLY, Jackson would omit it when serializing the record.
        // Build JSON manually to include password so validation passes.
        String json = "{" +
                "\"firstName\":\"Jane\"," +
                "\"middleName\":null," +
                "\"lastName\":\"Doe\"," +
                "\"username\":\"janedoe\"," +
                "\"password\":\"SecurePass123$\"," +
                "\"dateOfBirth\":\"1992-05-10\"," +
                "\"ssn\":\"123456789\"," +
                "\"gender\":\"FEMALE\"," +
                "\"email\":\"jane.doe@example.com\"," +
                "\"phoneNumber\":\"5550101234\"" +
                "}";

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.username", is("janedoe")));
    }

    @Test
    @DisplayName("GET /api/user/{id} with invalid Authorization returns 401")
    void getUser_invalidAuth_returns401() throws Exception {
        // AuthService will return empty for invalid header
        Mockito.when(authService.authenticate(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/{id}", 1L)
                        .header("Authorization", "Bearer notbasic"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/user/{id} with different authenticated user returns 403")
    void getUser_forbidden_whenIdMismatch() throws Exception {
        // Build a Basic header for username:pass (value irrelevant because we mock AuthService)
        String header = "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes());

        com.interview.models.user.User authUser = new com.interview.models.user.User();
        authUser.setId(99L); // does not match requested id 1

        Mockito.when(authService.authenticate(any())).thenReturn(Optional.of(authUser));

        mockMvc.perform(get("/api/user/{id}", 1L)
                        .header("Authorization", header))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/user/{id} success returns 200")
    void getUser_success_returns200() throws Exception {
        String header = "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes());

        com.interview.models.user.User authUser = new com.interview.models.user.User();
        authUser.setId(5L);
        Mockito.when(authService.authenticate(any())).thenReturn(Optional.of(authUser));

        UserResponse resp = new UserResponse(5L, "Ann", null, "Lee", "ann",
                LocalDate.of(1991,1,1), 30, Gender.FEMALE, "ann@example.com", "5550100000", null, null);
        Mockito.when(userService.getUserById(5L)).thenReturn(Optional.of(resp));

        mockMvc.perform(get("/api/user/{id}", 5L)
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.username", is("ann")));
    }
}
