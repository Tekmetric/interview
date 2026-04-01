package com.interview.controller.v1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class RepairOrderControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    public void testFullApiWorkflow() throws Exception {
        // List with filter
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repair-orders")
                        .param("status", "OPEN")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("readonly", "readonly123")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // GET single order to capture ETag
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repair-orders/1")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("readonly", "readonly123")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String etag = getResult.getResponse().getHeader("ETag");
        org.junit.jupiter.api.Assertions.assertNotNull(etag, "ETag should be present");

        // PUT with wrong If-Match ETag -> 409 Conflict
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/repair-orders/1")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("service", "service123"))
                        .header("If-Match", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        // PUT with correct If-Match ETag -> 200 OK and new ETag
        MvcResult putResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/repair-orders/1")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("service", "service123"))
                        .header("If-Match", etag)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String newEtag = putResult.getResponse().getHeader("ETag");
        org.junit.jupiter.api.Assertions.assertNotEquals(etag, newEtag, "ETag should change after update");

        // DELETE with latest ETag -> 204 No Content
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/repair-orders/1")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("service", "service123"))
                        .header("If-Match", newEtag))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Confirm it's really gone -> 404
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repair-orders/1")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("readonly", "readonly123")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testReadOnlyUserForbiddenOnUpdateDelete() throws Exception {
        // Attempt CREATE as READONLY user -> 403 Forbidden
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repair-orders")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("readonly", "readonly123"))
                        .header("If-Match", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "orderNumber": "RO-1002",
                              "vin": "1G1AK52F657513743",
                              "customerName": "John Smith",
                              "customerPhone": "555-1234",
                              "status": "IN_PROGRESS",
                              "lineItems": [
                                {"description": "Diagnosis", "quantity": 1, "unitPrice": 50.0}
                              ]
                            }
                        """))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // Attempt UPDATE as READONLY user -> 403 Forbidden
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/repair-orders/1")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("readonly", "readonly123"))
                        .header("If-Match", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // Attempt DELETE as READONLY user -> 403 Forbidden
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/repair-orders/1")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("readonly", "readonly123"))
                        .header("If-Match", "1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // Attempt AUDIT as READONLY user -> 403 Forbidden
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repair-orders/audit")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("readonly", "readonly123")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
