package com.interview.controller;

import static com.interview.TestResourceLoader.json;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EstimateControllerIntegrationTest {
    private static final String ESTIMATE_ID = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee";
    private static final String CUSTOMER_ID = "12121212-1212-1212-1212-121212121212";
    private static final String WORK_ORDER_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createEstimateAllowsNoWorkOrdersAndReturnsGeneratedId() throws Exception {
        mockMvc.perform(post("/api/estimates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("estimates/create-empty-estimate.json")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status", is("PENDING")))
            .andExpect(jsonPath("$.workOrders.length()", is(0)))
            .andExpect(jsonPath("$.totalTime", is(0)))
            .andExpect(jsonPath("$.totalCost", is(0)));
    }

    @Test
    void getEstimateByIdReturnsWorkOrderSummariesWithoutNestedParts() throws Exception {
        mockMvc.perform(get("/api/estimates/{id}", ESTIMATE_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(ESTIMATE_ID)))
            .andExpect(jsonPath("$.workOrders.length()", greaterThanOrEqualTo(2)))
            .andExpect(jsonPath("$.workOrders[0]", not(hasKey("partsNeeded"))));
    }

    @Test
    void listEstimatesSupportsPaginationAndFilters() throws Exception {
        mockMvc.perform(get("/api/estimates")
                .param("customerId", CUSTOMER_ID)
                .param("status", "PENDING")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.content[0].customerId", is(CUSTOMER_ID)))
            .andExpect(jsonPath("$.content[0].status", is("PENDING")));
    }

    @Test
    void updateEstimateChangesStatusOnly() throws Exception {
        String id = createEstimateAndReturnId("estimates/create-empty-estimate.json");

        mockMvc.perform(put("/api/estimates/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("estimates/update-estimate.json")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("APPROVED")))
            .andExpect(jsonPath("$.customerId", is(CUSTOMER_ID)))
            .andExpect(jsonPath("$.vehicleId", is("99999999-9999-9999-9999-999999999999")))
            .andExpect(jsonPath("$.workOrders.length()", is(0)))
            .andExpect(jsonPath("$.totalTime", is(0)))
            .andExpect(jsonPath("$.totalCost", is(0)));
    }

    @Test
    void addExistingWorkOrderAssociatesOneWorkOrderToEstimate() throws Exception {
        String id = createEstimateAndReturnId("estimates/create-empty-estimate.json");

        mockMvc.perform(post("/api/estimates/{estimateId}/work-orders/{workOrderId}", id, WORK_ORDER_ID))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.workOrders.length()", is(1)))
            .andExpect(jsonPath("$.workOrders[0].id", is(WORK_ORDER_ID)))
            .andExpect(jsonPath("$.totalTime", is(1.50)))
            .andExpect(jsonPath("$.totalCost", is(307.49)));
    }

    @Test
    void addExistingWorkOrderRejectsAlreadyAssociatedWorkOrders() throws Exception {
        mockMvc.perform(post("/api/estimates/{estimateId}/work-orders/{workOrderId}", ESTIMATE_ID, WORK_ORDER_ID))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is(
                "Work order aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa(Summary: Replace front brake pads) is already associated with estimate " + ESTIMATE_ID
            )));
    }

    @Test
    void addExistingWorkOrderReturnsNotFoundWhenWorkOrderIsMissing() throws Exception {
        String id = createEstimateAndReturnId("estimates/create-empty-estimate.json");

        mockMvc.perform(post("/api/estimates/{estimateId}/work-orders/{workOrderId}", id, "00000000-0000-0000-0000-000000000000"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Work order 00000000-0000-0000-0000-000000000000 was not found")));
    }

    @Test
    void addWorkOrderCreatesAndAssociatesWorkOrderToEstimate() throws Exception {
        String estimateId = createEstimateAndReturnId("estimates/create-empty-estimate.json");

        mockMvc.perform(post("/api/estimates/{estimateId}/work-orders", estimateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("work-orders/create-work-order.json")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(estimateId)))
            .andExpect(jsonPath("$.workOrders.length()", is(1)))
            .andExpect(jsonPath("$.workOrders[0].summary", is("Replace front brake pads")))
            .andExpect(jsonPath("$.workOrders[0].createdAt").exists())
            .andExpect(jsonPath("$.workOrders[0].updatedAt").exists())
            .andExpect(jsonPath("$.workOrders[0]", not(hasKey("partsNeeded"))));
    }

    @Test
    void deleteEstimateReturnsNoContent() throws Exception {
        String id = createEstimateAndReturnId("estimates/create-empty-estimate.json");

        mockMvc.perform(delete("/api/estimates/{id}", id))
            .andExpect(status().isNoContent());
    }

    @Test
    void getMissingEstimateReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/estimates/{id}", "00000000-0000-0000-0000-000000000000"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Estimate 00000000-0000-0000-0000-000000000000 was not found")));
    }

    @Test
    void createEstimateReturnsValidationErrors() throws Exception {
        mockMvc.perform(post("/api/estimates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("estimates/invalid-estimate.json")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Request validation failed")));
    }

    private String createEstimateAndReturnId(String resourcePath) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/estimates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(resourcePath)))
            .andExpect(status().isCreated())
            .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }
}
