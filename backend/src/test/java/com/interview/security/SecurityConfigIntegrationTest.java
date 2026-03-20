package com.interview.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.RepairOrderCreateDto;
import com.interview.dto.RepairOrderDto;
import com.interview.domain.RepairOrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class SecurityConfigIntegrationTest {

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
    void welcome_is_public_but_repairOrders_requires_auth() throws Exception {
        mockMvc.perform(get("/api/welcome"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/repair-orders?page=0&size=10"))
                .andExpect(status().isUnauthorized());

        // USER can read
        mockMvc.perform(get("/api/repair-orders?page=0&size=10")
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk());
    }

    @Test
    void delete_is_admin_only() throws Exception {
        RepairOrderCreateDto create = new RepairOrderCreateDto();
        create.setCustomerName("Test Customer");
        create.setDescription("Test description");
        create.setStatus(RepairOrderStatus.OPEN);

        MvcResult createResult = mockMvc.perform(post("/api/repair-orders")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        RepairOrderDto created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                RepairOrderDto.class
        );

        mockMvc.perform(delete("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("user", "user")))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/repair-orders/{id}", created.getId())
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());
    }
}
