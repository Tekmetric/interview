package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CarMakeCreateDto;
import com.interview.dto.CarMakeDto;
import com.interview.dto.CarMakeUpdateDto;
import com.interview.service.CarMakeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarMakeController.class)
@Import(GlobalExceptionHandler.class)
class CarMakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarMakeService service;

    @Test
    void create_returnsOkAndBody() throws Exception {
        CarMakeCreateDto createDto = new CarMakeCreateDto("Toyota", "Japan", 1937);
        CarMakeDto response = new CarMakeDto(1L, "Toyota", "Japan", 1937);
        given(service.create(any(CarMakeCreateDto.class))).willReturn(response);

        mockMvc.perform(post("/api/car-makes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Toyota")));
    }

    @Test
    void create_whenIllegalArgument_returnsBadRequest() throws Exception {
        CarMakeCreateDto createDto = new CarMakeCreateDto("Dup", "", 2000);
        given(service.create(any(CarMakeCreateDto.class))).willThrow(new IllegalArgumentException("Car make must be unique and valid."));

        mockMvc.perform(post("/api/car-makes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("unique")));
    }

    @Test
    void getOne_success() throws Exception {
        CarMakeDto dto = new CarMakeDto(5L, "Ford", "USA", 1903);
        given(service.getById(5L)).willReturn(dto);

        mockMvc.perform(get("/api/car-makes/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("Ford")));
    }

    @Test
    void getOne_notFound_returns404() throws Exception {
        given(service.getById(999L)).willThrow(new com.interview.model.CarMakeNotFoundException("not found"));

        mockMvc.perform(get("/api/car-makes/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void getAll_withoutFilter_returnsPage() throws Exception {
        CarMakeDto d1 = new CarMakeDto(1L, "Audi", "Germany", 1909);
        CarMakeDto d2 = new CarMakeDto(2L, "Volvo", "Sweden", 1927);
        Page<CarMakeDto> page = new PageImpl<>(Arrays.asList(d1, d2), PageRequest.of(0, 10), 2);
        given(service.getAll(isNull(), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/car-makes").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Audi")));
    }

    @Test
    void getAll_withFilter_usesServiceFilter() throws Exception {
        CarMakeDto d1 = new CarMakeDto(3L, "Toyota", "Japan", 1937);
        Page<CarMakeDto> page = new PageImpl<>(Collections.singletonList(d1), PageRequest.of(0, 10), 1);
        given(service.getAll(eq("toy"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/car-makes").param("name", "toy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Toyota")));
    }

    @Test
    void update_success() throws Exception {
        Long id = 10L;
        CarMakeUpdateDto updateDto = new CarMakeUpdateDto();
        updateDto.setName("NewName");
        CarMakeDto response = new CarMakeDto(id, "NewName", "USA", 2000);
        given(service.update(eq(id), any(CarMakeUpdateDto.class))).willReturn(response);

        mockMvc.perform(put("/api/car-makes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("NewName")));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        Long id = 77L;
        given(service.update(eq(id), any(CarMakeUpdateDto.class))).willThrow(new com.interview.model.CarMakeNotFoundException("nope"));

        mockMvc.perform(put("/api/car-makes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CarMakeUpdateDto())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("nope")));
    }

    @Test
    void update_illegalArgument_returns400() throws Exception {
        Long id = 5L;
        given(service.update(eq(id), any(CarMakeUpdateDto.class))).willThrow(new IllegalArgumentException("invalid"));

        mockMvc.perform(put("/api/car-makes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CarMakeUpdateDto())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("invalid")));
    }

    @Test
    void delete_success_returns204() throws Exception {
        Mockito.doNothing().when(service).delete(5L);

        mockMvc.perform(delete("/api/car-makes/{id}", 5))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        Mockito.doThrow(new com.interview.model.CarMakeNotFoundException("missing")).when(service).delete(5L);

        mockMvc.perform(delete("/api/car-makes/{id}", 5))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("missing")));
    }
}
