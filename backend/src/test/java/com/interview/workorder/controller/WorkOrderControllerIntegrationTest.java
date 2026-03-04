package com.interview.workorder.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WorkOrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE work_orders RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE customers RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        jdbcTemplate.execute("""
                INSERT INTO customers (name, created_at, updated_at, version)
                VALUES ('Seed Customer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                """);
        jdbcTemplate.execute("""
                INSERT INTO customers (name, created_at, updated_at, version)
                VALUES ('Another Customer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                """);
        jdbcTemplate.execute("""
                INSERT INTO work_orders (customer_id, vin, issue_description, status, created_at, updated_at, version)
                VALUES (1, '1HGCM82633A004352', 'Initial issue', 'OPEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                """);
    }

    @Test
    void shouldListWorkOrders() throws Exception {
        mockMvc.perform(get("/api/customers/1/work-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerId").value(1));
    }

    @Test
    void shouldCreateWorkOrder() throws Exception {
        Map<String, Object> payload = Map.of(
                "vin", "JH4KA9650MC012345",
                "issueDescription", "Oil change and filter",
                "status", "OPEN"
        );

        mockMvc.perform(post("/api/customers/1/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void shouldUpdateAndDeleteWorkOrder() throws Exception {
        Map<String, Object> updatePayload = Map.of(
                "vin", "1HGCM82633A004352",
                "issueDescription", "Issue resolved",
                "status", "COMPLETED"
        );

        mockMvc.perform(put("/api/customers/1/work-orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(delete("/api/customers/1/work-orders/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/customers/1/work-orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectWhenCustomerDoesNotExist() throws Exception {
        Map<String, Object> payload = Map.of(
                "vin", "JH4KA9650MC012345",
                "issueDescription", "Oil change and filter",
                "status", "OPEN"
        );

        mockMvc.perform(post("/api/customers/999/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer with id 999 was not found"));
    }

    @Test
    void shouldRejectWhenWorkOrderDoesNotBelongToCustomer() throws Exception {
        mockMvc.perform(get("/api/customers/2/work-orders/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("WorkOrder with id 1 was not found"));
    }
}
