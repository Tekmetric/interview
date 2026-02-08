package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.ServiceJobDTO;
import com.interview.model.enumeration.ServiceJobStatus;
import com.interview.service.ServiceJobService;
import com.interview.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceJobResource.class)
class ServiceJobResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceJobService serviceJobService;

    @Autowired
    private ObjectMapper objectMapper;

    private ServiceJobDTO serviceJobDTO;

    @BeforeEach
    void setUp() {
        serviceJobDTO = new ServiceJobDTO();
        serviceJobDTO.setId(1L);
        serviceJobDTO.setDescription("Oil change");
        serviceJobDTO.setCreationDate(Instant.now());
        serviceJobDTO.setStatus(ServiceJobStatus.PENDING);
        serviceJobDTO.setVehicleId(1L);
    }

    @Test
    void createServiceJob_shouldReturnCreated() throws Exception {
        ServiceJobDTO createDto = new ServiceJobDTO();
        createDto.setDescription("Oil change");
        createDto.setCreationDate(Instant.now());
        createDto.setStatus(ServiceJobStatus.PENDING);
        createDto.setVehicleId(1L);

        when(serviceJobService.create(any(ServiceJobDTO.class))).thenReturn(serviceJobDTO);

        mockMvc.perform(post("/api/service-jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createServiceJob_withInvalidData_shouldReturnBadRequest() throws Exception {
        ServiceJobDTO invalidServiceJob = new ServiceJobDTO();
        invalidServiceJob.setDescription(null); // Invalid

        mockMvc.perform(post("/api/service-jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidServiceJob)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateServiceJob_shouldReturnOk() throws Exception {
        when(serviceJobService.update(any(ServiceJobDTO.class))).thenReturn(serviceJobDTO);

        mockMvc.perform(put("/api/service-jobs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(serviceJobDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllServiceJobs_shouldReturnOk() throws Exception {
        Page<ServiceJobDTO> page = new PageImpl<>(Collections.singletonList(serviceJobDTO));
        when(serviceJobService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/service-jobs?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void getServiceJob_whenNotExists_shouldReturnNotFound() throws Exception {
        when(serviceJobService.findOne(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/service-jobs/99"))
            .andExpect(status().isNotFound());
    }
}
