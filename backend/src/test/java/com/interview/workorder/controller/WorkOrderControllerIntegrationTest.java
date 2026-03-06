package com.interview.workorder.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import com.interview.workorder.model.WorkOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
class WorkOrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListWorkOrders() throws Exception {
        mockMvc.perform(get("/api/customers/1/work-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].customerId").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void shouldFilterWorkOrdersByStatus() throws Exception {
        mockMvc.perform(get("/api/customers/1/work-orders")
                        .param("status", WorkOrderStatus.COMPLETED.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value(WorkOrderStatus.COMPLETED.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldPaginateWorkOrders() throws Exception {
        mockMvc.perform(get("/api/customers/1/work-orders")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(3))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalElements").value(3));
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
                "status", WorkOrderStatus.COMPLETED
        );

        mockMvc.perform(put("/api/customers/1/work-orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(WorkOrderStatus.COMPLETED.toString()));

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

    @Test
    void shouldGenerateRequestIdWhenHeaderMissing() throws Exception {
        mockMvc.perform(get("/api/customers/1/work-orders"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"));
    }

    @Test
    void shouldEchoRequestIdWhenHeaderProvided() throws Exception {
        mockMvc.perform(get("/api/customers/1/work-orders")
                        .header("X-Request-Id", "demo-request-id-123"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", "demo-request-id-123"));
    }
}
