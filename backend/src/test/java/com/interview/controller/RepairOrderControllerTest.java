package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.RepairOrderCreateDto;
import com.interview.dto.RepairOrderDto;
import com.interview.dto.RepairOrderUpdateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class RepairOrderControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void unauthorized_access_is_rejected() throws Exception {
        mockMvc.perform(get("/api/repair-orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_as_admin_returns_201() throws Exception {
        RepairOrderDto created = createRepairOrderAsAdmin();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getVersion()).isNotNull();
    }

    @Test
    void create_as_user_returns_403() throws Exception {
        RepairOrderCreateDto create = new RepairOrderCreateDto();
        create.setCustomerName("Test Customer");
        create.setDescription("Test description");
        create.setStatus(com.interview.domain.RepairOrderStatus.OPEN);

        mockMvc.perform(post("/api/repair-orders")
                        .with(httpBasic("user", "user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isForbidden());
    }

    @Test
    void get_by_id_as_user_returns_200() throws Exception {
        RepairOrderDto created = createRepairOrderAsAdmin();

        mockMvc.perform(get("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk());
    }

    @Test
    void list_as_user_returns_200() throws Exception {
        mockMvc.perform(get("/api/repair-orders?page=0&size=10")
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk());
    }

    @Test
    void update_as_admin_returns_200() throws Exception {
        RepairOrderDto created = createRepairOrderAsAdmin();
        RepairOrderUpdateDto update = buildUpdate(created, "Updated Customer", "Updated description",
                com.interview.domain.RepairOrderStatus.COMPLETED);

        mockMvc.perform(put("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }

    @Test
    void update_as_user_returns_403() throws Exception {
        RepairOrderDto created = createRepairOrderAsAdmin();
        RepairOrderUpdateDto update = buildUpdate(created, "Updated Customer", "Updated description",
                com.interview.domain.RepairOrderStatus.COMPLETED);

        mockMvc.perform(put("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("user", "user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_as_admin_returns_204() throws Exception {
        RepairOrderDto created = createRepairOrderAsAdmin();

        mockMvc.perform(delete("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_as_user_returns_403() throws Exception {
        RepairOrderDto created = createRepairOrderAsAdmin();

        mockMvc.perform(delete("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("user", "user")))
                .andExpect(status().isForbidden());
    }

    @Test
    void get_after_delete_returns_404() throws Exception {
        RepairOrderDto created = createRepairOrderAsAdmin();

        // Setup: delete first so the action under test is GET returning 404.
        mockMvc.perform(delete("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound());
    }

    private RepairOrderDto createRepairOrderAsAdmin() throws Exception {
        RepairOrderCreateDto create = new RepairOrderCreateDto();
        create.setCustomerName("Test Customer");
        create.setDescription("Test description");
        create.setStatus(com.interview.domain.RepairOrderStatus.OPEN);

        MvcResult createResult = mockMvc.perform(post("/api/repair-orders")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                RepairOrderDto.class
        );
    }

    private RepairOrderUpdateDto buildUpdate(
            RepairOrderDto created,
            String customerName,
            String description,
            com.interview.domain.RepairOrderStatus status
    ) {
        RepairOrderUpdateDto update = new RepairOrderUpdateDto();
        update.setVersion(created.getVersion());
        update.setCustomerName(customerName);
        update.setDescription(description);
        update.setStatus(status);
        return update;
    }
}
