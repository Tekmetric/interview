package com.interview.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.repository.model.Genre;
import com.interview.resource.model.BookDto.BookResponse;
import com.interview.resource.model.BookDto.CreateRequest;
import com.interview.resource.model.BookDto.UpdateRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookApiIntegrationTest {

    private static final String BASE_URL = "/api/v1/books";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private static UUID createdBookId;
    private static Long createdBookVersion;

    @Test
    @Order(1)
    @DisplayName("POST — 201 Created with Location header")
    void shouldCreateBook() throws Exception {
        String body = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(createRequest("978-0306406157"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.isbn").value("978-0306406157"))
                .andExpect(jsonPath("$.version").value(0))
                .andReturn().getResponse().getContentAsString();

        BookResponse response = objectMapper.readValue(body, BookResponse.class);
        createdBookId      = response.id();
        createdBookVersion = response.version();
    }

    @Test
    @Order(2)
    @DisplayName("POST — 409 Conflict on duplicate ISBN")
    void shouldRejectDuplicateIsbn() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(createRequest("978-0306406157"))))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value(containsString("duplicate-isbn")));
    }

    @Test
    @Order(3)
    @DisplayName("POST — 400 Bad Request on invalid payload")
    void shouldRejectInvalidPayload() throws Exception {
        String badJson = """
                { "title": "", "author": "", "isbn": "not-an-isbn", "price": -5.00 }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    @Order(4)
    @DisplayName("POST with Idempotency-Key — replay returns same book ID")
    void shouldHandleIdempotentCreate() throws Exception {
        String key = UUID.randomUUID().toString();
        String payload = json(createRequest("978-1-56619-909-4"));

        MvcResult first = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", key)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult second = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", key)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        String firstId  = objectMapper.readTree(first.getResponse().getContentAsString()).get("id").asText();
        String secondId = objectMapper.readTree(second.getResponse().getContentAsString()).get("id").asText();
        assertThat(firstId).isEqualTo(secondId);
    }

    @Test
    @Order(5)
    @DisplayName("GET /{id} — 200 OK")
    void shouldGetBook() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", createdBookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdBookId.toString()))
                .andExpect(jsonPath("$.version").isNumber());
    }

    @Test
    @Order(6)
    @DisplayName("GET /{id} — 404 for unknown ID")
    void shouldReturn404ForUnknownId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value(containsString("book-not-found")));
    }

    @Test
    @Order(7)
    @DisplayName("GET / — returns paginated list")
    void shouldListBooks() throws Exception {
        mockMvc.perform(get(BASE_URL).param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.number").value(0));  // Spring Page uses "number" for page index
    }

    @Test
    @Order(8)
    @DisplayName("GET /?genre=TECHNOLOGY — filters by genre")
    void shouldFilterByGenre() throws Exception {
        mockMvc.perform(get(BASE_URL).param("genre", "TECHNOLOGY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].genre", everyItem(is("TECHNOLOGY"))));
    }

    @Test
    @Order(9)
    @DisplayName("GET /?query=clean — full-text search on title")
    void shouldSearchByQuery() throws Exception {
        mockMvc.perform(get(BASE_URL).param("query", "Clean Code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title", containsStringIgnoringCase("clean")));
    }

    @Test
    @Order(10)
    @DisplayName("GET /?sort=price,asc — custom sort")
    void shouldSortByPrice() throws Exception {
        mockMvc.perform(get(BASE_URL).param("sort", "price,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(11)
    @DisplayName("PUT /{id} — 200 OK with correct version")
    void shouldUpdateBook() throws Exception {
        UpdateRequest req = new UpdateRequest(
                "Clean Code — Updated", "Robert C. Martin",
                "978-0306406157", Genre.TECHNOLOGY,
                new BigDecimal("39.99"), LocalDate.of(2020, 6, 1),
                "Updated description.", createdBookVersion);

        String body = mockMvc.perform(put(BASE_URL + "/{id}", createdBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code — Updated"))
                .andExpect(jsonPath("$.version").value(greaterThan(0)))
                .andReturn().getResponse().getContentAsString();

        createdBookVersion = objectMapper.readValue(body, BookResponse.class).version();
    }

    @Test
    @Order(12)
    @DisplayName("PUT with Idempotency-Key — replay returns same response")
    void shouldHandleIdempotentUpdate() throws Exception {
        String key = UUID.randomUUID().toString();
        UpdateRequest req = new UpdateRequest(
                "Clean Code — Idempotent", "Robert C. Martin",
                "978-0306406157", Genre.TECHNOLOGY,
                new BigDecimal("41.00"), LocalDate.of(2020, 6, 1),
                null, createdBookVersion);

        MvcResult first = mockMvc.perform(put(BASE_URL + "/{id}", createdBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", key)
                        .content(json(req)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult second = mockMvc.perform(put(BASE_URL + "/{id}", createdBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", key)
                        .content(json(req)))
                .andExpect(status().isOk())
                .andReturn();

        String firstVersion  = objectMapper.readTree(first.getResponse().getContentAsString()).get("version").asText();
        String secondVersion = objectMapper.readTree(second.getResponse().getContentAsString()).get("version").asText();
        assertThat(firstVersion).isEqualTo(secondVersion);

        createdBookVersion = objectMapper.readValue(
                first.getResponse().getContentAsString(), BookResponse.class).version();
    }
    @Test
    @Order(13)
    @DisplayName("DELETE /{id} — 204 No Content")
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", createdBookId))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(14)
    @DisplayName("DELETE /{id} — 404 after deletion")
    void shouldReturn404AfterDeletion() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", createdBookId))
                .andExpect(status().isNotFound());
    }

    private CreateRequest createRequest(String isbn) {
        return new CreateRequest("Test Book", "Test Author", isbn,
                Genre.TECHNOLOGY, new BigDecimal("29.99"),
                LocalDate.of(2020, 1, 1), "A test book.");
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}