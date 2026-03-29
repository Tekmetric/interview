package com.interview.resource.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.resource.dto.AutoShopRequest;
import com.interview.resource.entity.AutoShop;
import com.interview.resource.mocks.AutoShopMocks;
import com.interview.resource.repository.IAutoShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AutoShopIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private AutoShopMocks mocks;

    @Autowired
    private IAutoShopRepository repository;

    private AutoShop shop;

   @BeforeEach
    void setup() {
        repository.deleteAll();

        shop =  repository.save(
                new AutoShop(null,"Discount Tires", "123 Main St", "Atlanta", "GA", 5)
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAutoShopById() throws Exception {
        mockMvc.perform(get("/api/autoShops/{id}", shop.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Discount Tires"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllAutoShops() throws Exception {
        mockMvc.perform(get("/api/autoShops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateAutoShop() throws Exception {
        AutoShopRequest request = mocks.createShopRequest2();

        mockMvc.perform(post("/api/autoShops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Star Mechanics"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdate() throws Exception {
        AutoShopRequest request = mocks.createShopRequest();

        mockMvc.perform(put("/api/autoShops/{id}", shop.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Long Star Mechanics"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDelete() throws Exception {
        mockMvc.perform(delete("/api/autoShops/{id}", shop.getId()))
                .andExpect(status().isNoContent());

        assertEquals(repository.findById(shop.getId()), Optional.empty());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldRejectCreateNonAdmin() throws Exception {
        AutoShopRequest request = mocks.createShopRequest2();

        mockMvc.perform(post("/api/autoShops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
