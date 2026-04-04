package com.interview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAllUsers_shouldReturn200_withAuth() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllUsers_shouldReturn401_withoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createUser_shouldReturn201_withValidData() throws Exception {
        String userJson = """
                {
                    "name": "John Doe",
                    "email": "john@example.com",
                    "age": 30
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getUserById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get("/api/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateUser_shouldReturn200_whenExists() throws Exception {
        // First create a user
        String userJson = """
                {
                    "name": "Original Name",
                    "email": "original@example.com",
                    "age": 30
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                .with(httpBasic("user", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

        // Now update the user
        String updateJson = """
                {
                    "name": "Updated Name",
                    "email": "updated@example.com",
                    "age": 35
                }
                """;

        mockMvc.perform(put("/api/v1/users/1")
                .with(httpBasic("user", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteUser_shouldReturn204_whenExists() throws Exception {
        // First create a user
        String userJson = """
                {
                    "name": "User to Delete",
                    "email": "delete@example.com",
                    "age": 40
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                .with(httpBasic("user", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

        // Now delete the user
        mockMvc.perform(delete("/api/v1/users/1")
                .with(httpBasic("user", "password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void welcomeEndpoint_shouldReturn200_withoutAuth() throws Exception {
        mockMvc.perform(get("/api/welcome")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to the interview project!"));
    }
}