package com.interview.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TeeTimeControllerTest {
    private static final String TEE_TIME_URL = "/api/tee-times";
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetTeeTimes() throws Exception {
        mockMvc.perform(get(TEE_TIME_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetTeeTimeById() throws Exception {
        mockMvc.perform(get(TEE_TIME_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.time").value("2021-06-01 08:00:00"))
                .andExpect(jsonPath("$.players").value("John Doe, Jane Doe"))
                .andExpect(jsonPath("$.course").value("Pine Valley"));
    }

    @Test
    public void testGetTeeTimeByIdNotFound() throws Exception {
        mockMvc.perform(get(TEE_TIME_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createNewTeeTime() throws Exception {
        final String teeTime = "{\"time\":\"2021-08-01T12:00:00Z\",\"players\":\"Alice, Bob, Charlie\",\"course\":\"Pebble Beach\"}";
        mockMvc.perform(post(TEE_TIME_URL)
                .content(teeTime).contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateTeeTime() throws Exception {
        final String teeTime = "{\"time\":\"2021-08-01T12:00:00Z\",\"players\":\"Alice, Bob, Charlie\",\"course\":\"Pebble Beach\"}";
        mockMvc.perform(put(TEE_TIME_URL + "/2")
                .content(teeTime).contentType("application/json"))
                .andExpect(status().isOk());
        mockMvc.perform(get(TEE_TIME_URL + "/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.time").value("2021-08-01T12:00:00Z"))
                .andExpect(jsonPath("$.players").value("Alice, Bob, Charlie"))
                .andExpect(jsonPath("$.course").value("Pebble Beach"));
    }

    @Test
    public void testUpdateTeeTimeNotFound() throws Exception {
        final String teeTime = "{\"time\":\"2021-08-01T12:00:00Z\",\"players\":\"Alice, Bob, Charlie\",\"course\":\"Pebble Beach\"}";
        mockMvc.perform(put(TEE_TIME_URL + "/99")
                .content(teeTime).contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTeeTime() throws Exception {
        mockMvc.perform(get(TEE_TIME_URL + "/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTeeTimeNotFound() throws Exception {
        mockMvc.perform(get(TEE_TIME_URL + "/99"))
                .andExpect(status().isNotFound());
    }
}
