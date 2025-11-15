package com.interview.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CarMakeCreateDto;
import com.interview.dto.CarMakeUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarMakeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/car-makes returns a page of data")
    void list_returnsSeededData() throws Exception {
        mockMvc.perform(get("/api/car-makes").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(org.hamcrest.Matchers.equalTo(5)))
                .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @DisplayName("GET /api/car-makes?name=toy filters by name (case-insensitive)")
    void list_withFilter() throws Exception {
        mockMvc.perform(get("/api/car-makes").param("name", "toy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(org.hamcrest.Matchers.containsStringIgnoringCase("toy")));
    }

    @Test
    @DisplayName("POST -> persist -> GET by id")
    void create_and_getById() throws Exception {
        CarMakeCreateDto create = new CarMakeCreateDto("Rivian", "USA", 2009);

        MvcResult postResult = mockMvc.perform(post("/api/car-makes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Rivian"))
                .andReturn();

        JsonNode created = objectMapper.readTree(postResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
        long id = created.get("id").asLong();

        mockMvc.perform(get("/api/car-makes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) id))
                .andExpect(jsonPath("$.name").value("Rivian"))
                .andExpect(jsonPath("$.country").value("USA"))
                .andExpect(jsonPath("$.foundedYear").value(2009));
    }

    @Test
    @DisplayName("POST duplicate name returns 400 Bad Request")
    void create_duplicateName_returnsBadRequest() throws Exception {
        // "Toyota" is inserted from data.sql
        CarMakeCreateDto create = new CarMakeCreateDto("Toyota", "Japan", 1937);

        mockMvc.perform(post("/api/car-makes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT update then verify updated fields are persisted")
    void update_success() throws Exception {
        // first create
        CarMakeCreateDto create = new CarMakeCreateDto("TestBrand", "Nowhere", 1999);
        MvcResult createdRes = mockMvc.perform(post("/api/car-makes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andReturn();
        long id = objectMapper.readTree(createdRes.getResponse().getContentAsString()).get("id").asLong();

        // update
        CarMakeUpdateDto update = new CarMakeUpdateDto();
        update.setName("TestBrand2");
        update.setCountry("Somewhere");
        update.setFoundedYear(2001);

        mockMvc.perform(put("/api/car-makes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestBrand2"))
                .andExpect(jsonPath("$.country").value("Somewhere"))
                .andExpect(jsonPath("$.foundedYear").value(2001));

        // verify persisted
        mockMvc.perform(get("/api/car-makes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestBrand2"));
    }

    @Test
    @DisplayName("PUT on non-existent id returns 404")
    void update_notFound() throws Exception {
        CarMakeUpdateDto update = new CarMakeUpdateDto();
        update.setName("Nope");

        mockMvc.perform(put("/api/car-makes/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE then ensure 404 on subsequent GET")
    void delete_thenNotFound() throws Exception {
        // create first
        CarMakeCreateDto create = new CarMakeCreateDto("DeleteMe", "X", 2000);
        MvcResult res = mockMvc.perform(post("/api/car-makes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andReturn();
        long id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        // delete
        mockMvc.perform(delete("/api/car-makes/{id}", id))
                .andExpect(status().isNoContent());

        // subsequent GET should be 404
        mockMvc.perform(get("/api/car-makes/{id}", id))
                .andExpect(status().isNotFound());
    }
}
