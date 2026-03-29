package com.interview.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.resource.config.SecurityConfig;
import com.interview.resource.dto.AutoShopRequest;
import com.interview.resource.dto.AutoShopResponse;
import com.interview.resource.mocks.AutoShopMocks;
import com.interview.resource.service.AutoShopService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AutoShopController.class)
@Import(SecurityConfig.class)
public class AutoShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutoShopService service;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private AutoShopMocks mocks;

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAutoShopById() throws Exception {
        AutoShopResponse response = mocks.createShopResponse();

        when(service.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/autoShops/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Long Star Mechanics"));

        verify(service).getById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllAutoShops() throws Exception {
        List<AutoShopResponse> responses =  mocks.getAutoShopsResponses();

        when(service.getAll()).thenReturn(responses);

        mockMvc.perform(get("/api/autoShops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Discount Tires"));

        verify(service).getAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateAutoShop() throws Exception {
        AutoShopRequest request =  mocks.createShopRequest();

        AutoShopResponse response =  mocks.createShopResponse();

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/autoShops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Long Star Mechanics"));

        verify(service).create(any(AutoShopRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateAutoShop() throws Exception {
        AutoShopRequest request =  mocks.createShopRequest();

        AutoShopResponse response =  mocks.createShopResponse();

        when(service.update(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/autoShops/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Long Star Mechanics"));

        verify(service).update(eq(1L), any(AutoShopRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteAutoShop() throws Exception {

        mockMvc.perform(delete("/api/autoShops/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreateAutoShop() throws Exception {
        AutoShopRequest request =  mocks.createShopRequest();

        mockMvc.perform(post("/api/autoShops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(service, never()).create(any());
    }

    @Test
    void unauthenticatedUserIsRejected() throws Exception {
        mockMvc.perform(get("/api/autoShops"))
                .andExpect(status().isForbidden());
    }

}
