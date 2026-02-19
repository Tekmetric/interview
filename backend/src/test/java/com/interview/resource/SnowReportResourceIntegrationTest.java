package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.SnowReportRequest;
import com.interview.entity.SnowReport;
import com.interview.repository.SnowReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/test-data.sql")
class SnowReportResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SnowReportRepository snowReportRepository;

    @Test
    void findAllShouldReturn200WithPagedContent() throws Exception {
        mockMvc.perform(get("/api/snow-reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        SnowReport record = snowReportRepository.findAll().get(0);

        mockMvc.perform(get("/api/snow-reports/" + record.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mountainName").value(record.getMountainName()));
    }

    @Test
    void findByIdShouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/snow-reports/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn201() throws Exception {
        SnowReportRequest request = new SnowReportRequest();
        request.setMountainName("Breckenridge");
        request.setRegion("Colorado");
        request.setCountry("USA");
        request.setCurrentSnowTotal(75);

        mockMvc.perform(post("/api/snow-reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.mountainName").value("Breckenridge"))
                .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void createWithNoRegionShouldReturn201() throws Exception {
        SnowReportRequest request = new SnowReportRequest();
        request.setMountainName("Matterhorn");
        request.setCountry("Switzerland");
        request.setCurrentSnowTotal(120);

        mockMvc.perform(post("/api/snow-reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.mountainName").value("Matterhorn"))
                .andExpect(jsonPath("$.region").doesNotExist());
    }

    @Test
    void updateShouldReturn200() throws Exception {
        SnowReport existing = snowReportRepository.findAll().get(0);

        SnowReportRequest request = new SnowReportRequest();
        request.setMountainName(existing.getMountainName());
        request.setRegion(existing.getRegion());
        request.setCountry(existing.getCountry());
        request.setCurrentSnowTotal(999);

        mockMvc.perform(put("/api/snow-reports/" + existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentSnowTotal").value(999));
    }

    @Test
    void updateShouldReturn404WhenNotFound() throws Exception {
        SnowReportRequest request = new SnowReportRequest();
        request.setMountainName("Ghost Mountain");
        request.setCountry("USA");
        request.setCurrentSnowTotal(0);

        mockMvc.perform(put("/api/snow-reports/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        SnowReport existing = snowReportRepository.findAll().get(0);

        mockMvc.perform(delete("/api/snow-reports/" + existing.getId()))
                .andExpect(status().isNoContent());
    }
}