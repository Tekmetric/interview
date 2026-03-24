package com.interview.transactions.integration;

import com.interview.Application;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = WebEnvironment.RANDOM_PORT

)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    @Order(1)
    void getAllTransactions_initially_ReturnsEmptyList() {
        webClient
                .get()
                .uri("/api/transactions")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }

    @Test
    @Order(2)
    void createTransaction_shouldReturn201_andValidTransaction() {
        String body = """
            {
                "amount": 100.00,
                "currency": "EUR"
            }
            """;

        webClient
                .post()
                .uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.amount").isEqualTo(100.00)
                .jsonPath("$.currency").isEqualTo("EUR")
                .jsonPath("$.status").isEqualTo("PLACED");
    }

    @Test
    @Order(2)
    void createTransaction_withInvalidAmount_shouldReturn400() {
        String body = """
            {
                "amount": 0.00,
                "currency": "EUR"
            }
            """;

        webClient
                .post()
                .uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getTransactionById_existing_Returns200() {
        // assume you have a way to create a transaction first
        Long id = createTransaction();

        webClient
                .get()
                .uri("/api/transactions/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.amount").isNumber()
                .jsonPath("$.currency").isNotEmpty()
                .jsonPath("$.status").isNotEmpty();
    }

    @Test
    void getTransactionById_notFound_Returns404() {
        webClient
                .get()
                .uri("/api/transactions/999999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateTransaction_existing_Returns200() {
        Long id = createTransaction();

        String body = """
            {
                "amount": 150.00,
                "currency": "USD",
                "status": "FINALIZED"
            }
            """;

        webClient
                .put()
                .uri("/api/transactions/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.amount").isEqualTo(150.00)
                .jsonPath("$.currency").isEqualTo("USD")
                .jsonPath("$.status").isEqualTo("FINALIZED");
    }

    @Test
    void updateTransaction_notFound_Returns404() {
        String body = """
            {
                "amount": 150.00,
                "currency": "USD",
                "status": "FINALIZED"
            }
            """;

        webClient
                .put()
                .uri("/api/transactions/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteTransaction_existing_Returns204() {
        Long id = createTransaction();

        webClient
                .delete()
                .uri("/api/transactions/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteTransaction_notFound_Returns404() {
        webClient
                .delete()
                .uri("/api/transactions/999999")
                .exchange()
                .expectStatus().isNotFound();
    }

    // Helper to create a transaction for update/delete tests
    private Long createTransaction() {
        String body = """
            {
                "amount": 100.00,
                "currency": "EUR"
            }
            """;

        AtomicLong id = new AtomicLong(-1L);

        webClient
                .post()
                .uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id")
                .value(Long.class, id::set);

        return id.get();
    }
}
