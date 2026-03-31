package com.interview.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.RepairOrderRequest;
import com.interview.entity.RepairOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full application tests that verify controller, service, repository,
 * and database integration together.
 */
@SpringBootTest
@AutoConfigureMockMvc
// Reset application state between tests because the H2 database is shared in the Spring context.
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RepairOrderApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void seededRowsLoad() throws Exception {
        mockMvc.perform(get("/api/repair-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customerName").value("Jane Doe"))
                .andExpect(jsonPath("$[1].customerName").value("John Smith"));
    }

    @Test
    void createWorks() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("John Doe");
        request.setVehicleVin("JM1BK32F581234567");
        request.setDescription("Check engine light diagnosis");
        request.setStatus(RepairOrderStatus.OPEN);
        request.setTotalCost(new BigDecimal("149.99"));

        mockMvc.perform(post("/api/repair-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.vehicleVin").value("JM1BK32F581234567"))
                .andExpect(jsonPath("$.description").value("Check engine light diagnosis"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.totalCost").value(149.99));
    }

    @Test
    void updateWorks() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("Jane Smith");
        request.setVehicleVin("1HGCM82633A004352");
        request.setDescription("Brake pad replacement completed");
        request.setStatus(RepairOrderStatus.COMPLETED);
        request.setTotalCost(new BigDecimal("350.00"));

        mockMvc.perform(put("/api/repair-orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Brake pad replacement completed"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.totalCost").value(350.00));
    }

    @Test
    void deleteWorks() throws Exception {
        mockMvc.perform(delete("/api/repair-orders/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/repair-orders/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateVinReturns409() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("Duplicate Test");
        request.setVehicleVin("1HGCM82633A004352");
        request.setDescription("Trying duplicate VIN");
        request.setStatus(RepairOrderStatus.OPEN);
        request.setTotalCost(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/repair-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
