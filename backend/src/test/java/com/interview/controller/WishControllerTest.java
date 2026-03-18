package com.interview.controller;

import com.interview.dto.WishDTO;
import com.interview.dto.WishLightDTO;
import com.interview.exception.WishNotFoundException;
import com.interview.service.WishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishController.class)
class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishService wishService;

    @Autowired
    private JsonMapper jsonMapper;

    private WishDTO wishDTO;
    private WishLightDTO wishLightDTO;

    @BeforeEach
    void setUp() {
        wishDTO = new WishDTO();
        wishDTO.setId(1L);
        wishDTO.setName("Test Wish");
        wishDTO.setComment("Test Comment");
        wishDTO.setLink("http://test.com");
        wishDTO.setCameTrue(false);

        wishLightDTO = new WishLightDTO("Test Wish", false);
    }

    @Test
    void getAllWishes_ShouldReturnList() throws Exception {
        List<WishLightDTO> wishes = Collections.singletonList(wishLightDTO);
        when(wishService.getAllWishes()).thenReturn(wishes);

        mockMvc.perform(get("/api/wishes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Wish"))
                .andExpect(jsonPath("$[0].cameTrue").value(false));
    }

    @Test
    void getWishById_ShouldReturnWish() throws Exception {
        when(wishService.getWishById(1L)).thenReturn(wishDTO);

        mockMvc.perform(get("/api/wishes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Wish"));
    }

    @Test
    void getWishById_NotFound_ShouldReturn404() throws Exception {
        when(wishService.getWishById(1L)).thenThrow(new WishNotFoundException("Wish not found with id: 1"));

        mockMvc.perform(get("/api/wishes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Wish not found with id: 1"));
    }

    @Test
    void createWish_ShouldReturnCreated() throws Exception {
        when(wishService.createWish(any(WishDTO.class))).thenReturn(wishDTO);

        mockMvc.perform(post("/api/wishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(wishDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Wish"));
    }

    @Test
    void createWish_InvalidData_ShouldReturn400() throws Exception {
        WishDTO invalidWish = new WishDTO();
        invalidWish.setName(""); // Invalid: NotBlank

        mockMvc.perform(post("/api/wishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(invalidWish)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"));
    }

    @Test
    void updateWish_ShouldReturnUpdated() throws Exception {
        when(wishService.updateWish(eq(1L), any(WishDTO.class))).thenReturn(wishDTO);

        mockMvc.perform(put("/api/wishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(wishDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Wish"));
    }

    @Test
    void deleteWish_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/wishes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void markAsCameTrue_ShouldReturnUpdated() throws Exception {
        wishDTO.setCameTrue(true);
        when(wishService.markAsCameTrue(1L)).thenReturn(wishDTO);

        mockMvc.perform(patch("/api/wishes/1/came-true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cameTrue").value(true));
    }
}
