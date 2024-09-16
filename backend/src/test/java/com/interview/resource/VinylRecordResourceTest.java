package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.domain.Artist;
import com.interview.service.ArtistService;
import com.interview.service.VinylRecordService;
import com.interview.service.dto.VinylRecordPayloadDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class VinylRecordResourceTest {

    public static final String API_VINYL_RECORD_PATH = "/api/vinyl";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VinylRecordService vinylRecordService;

    @MockBean
    private ArtistService artistService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createNewRecord_shouldReturn200() throws Exception {
        when(vinylRecordService.isAlbumAlreadyInserted(anyString(), anyList())).thenReturn(false);
        when(artistService.getAllByIdsIn(List.of(1L))).thenReturn(List.of(new Artist()));

        var vinylRecordDTO = new VinylRecordPayloadDTO();
        vinylRecordDTO.setTitle("Test Title");
        vinylRecordDTO.setArtistIds(List.of(1L));

        var jsonContent = objectMapper.writeValueAsString(vinylRecordDTO);
        mockMvc.perform(post(API_VINYL_RECORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
    }

    @Test
    void createNewRecordWithNoArtists_shouldReturn400() throws Exception {
        var vinylRecordDTO = new VinylRecordPayloadDTO();
        vinylRecordDTO.setTitle("Test Title");

        var jsonContent = objectMapper.writeValueAsString(vinylRecordDTO);
        mockMvc.perform(post(API_VINYL_RECORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createNewRecordWithNoTitle_shouldReturn400() throws Exception {
        var vinylRecordDTO = new VinylRecordPayloadDTO();
        vinylRecordDTO.setArtistIds(List.of(1L));

        var jsonContent = objectMapper.writeValueAsString(vinylRecordDTO);
        mockMvc.perform(post(API_VINYL_RECORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest());
    }
}