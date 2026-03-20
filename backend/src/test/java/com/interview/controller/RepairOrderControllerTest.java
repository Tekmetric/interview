package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.RepairOrderCreateDto;
import com.interview.dto.RepairOrderDto;
import com.interview.dto.RepairOrderUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RepairOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void unauthorized_access_is_rejected() throws Exception {
        mockMvc.perform(get("/api/repair-orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crud_flow_works_with_basic_auth() throws Exception {
        RepairOrderCreateDto create = new RepairOrderCreateDto();
        create.setCustomerName("Test Customer");
        create.setDescription("Test description");
        create.setStatus(com.interview.domain.RepairOrderStatus.OPEN);

        MvcResult createResult = mockMvc.perform(post("/api/repair-orders")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        RepairOrderDto created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                RepairOrderDto.class
        );

        assertThat(created.getId()).isNotNull();
        assertThat(created.getVersion()).isNotNull();

        // Update using optimistic concurrency version
        RepairOrderUpdateDto update = new RepairOrderUpdateDto();
        update.setVersion(created.getVersion());
        update.setCustomerName("Updated Customer");
        update.setDescription("Updated description");
        update.setStatus(com.interview.domain.RepairOrderStatus.COMPLETED);

        mockMvc.perform(put("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }
}
