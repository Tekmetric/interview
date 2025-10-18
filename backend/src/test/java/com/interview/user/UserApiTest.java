package com.interview.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserApiTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void testGetAll() {
        assertThat(mvc.get().uri("/api/users"))
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .isLenientlyEqualTo("""
                [{
                    "name": "Bob",
                    "surname": "Smith",
                    "email": "bsmith@email.com",
                    "version": 0
                }]
                """);
    }

    @Test
    void testGetUser() {
        assertThat(mvc.get().uri("/api/users/1"))
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .isLenientlyEqualTo("""
                {
                    "name": "Bob",
                    "surname": "Smith",
                    "email": "bsmith@email.com",
                    "version": 0
                }
                """);
    }

    @Test
    void testCreateUser() {
        String postBody = """
            {
                "name": "John",
                "surname": "Smith",
                "email": "jsmith@email.com"
            }
            """;
        assertThat(mvc.post().uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(postBody)
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .isLenientlyEqualTo("""
                {
                    "name": "John",
                    "surname": "Smith",
                    "email": "jsmith@email.com",
                    "version": 0
                }
                """);
        assertThat(mvc.get().uri("/api/users/2"))
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .isLenientlyEqualTo("""
                {
                    "name": "John",
                    "surname": "Smith",
                    "email": "jsmith@email.com",
                    "version": 0
                }
                """);
    }

    @Test
    void testDeleteUser() {
        assertThat(mvc.delete().uri("/api/users/1"))
            .hasStatus(HttpStatus.OK);
        assertThat(mvc.get().uri("/api/users"))
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .isLenientlyEqualTo("""
                []
                """);
        assertThat(mvc.get().uri("/api/users/1"))
            .hasStatus(HttpStatus.NOT_FOUND)
            .bodyText()
            .isEqualTo("User not found: 1");
    }

    @Test
    void testEditUser() {
        String putBody = """
            {
                "name": "Bob",
                "surname": "Smith",
                "email": "bobsmith@email.com",
                "version": 0
            }
            """;
        assertThat(mvc.put().uri("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(putBody)
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .isLenientlyEqualTo("""
                {
                    "name": "Bob",
                    "surname": "Smith",
                    "email": "bobsmith@email.com",
                    "version": 1
                }
                """);
    }

    @Test
    void testEditWithNoVersionFails() {
        String putBody = """
            {
                "name": "Bob",
                "surname": "Smith",
                "email": "bobsmith@email.com",
            }
            """;
        assertThat(mvc.put().uri("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(putBody)
        )
            .hasStatus(HttpStatus.BAD_REQUEST);
    }
}
