package com.interview.controller;

import com.interview.model.Athlete;
import com.interview.model.Position;
import com.interview.model.Shoots;
import com.interview.service.AthleteNotFoundException;
import com.interview.service.AthleteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AthleteController.class)
class AthleteControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AthleteService athleteService;

    @Test
    void createAthlete_missingFirstName_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lastName": "Crosby",
                                  "position": "C",
                                  "shoots": "L",
                                  "number": 87
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_error"))
                .andExpect(jsonPath("$.fieldErrors.firstName").exists());
    }

    @Test
    void createAthlete_invalidEnumValue_returns400BadRequest() throws Exception {
        mockMvc.perform(post("/api/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Sidney",
                                  "lastName": "Crosby",
                                  "position": "CENTER",
                                  "shoots": "L",
                                  "number": 87
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.message").value("Malformed JSON or invalid enum value"));
    }

    @Test
    void updateAthlete_notFound_returns404() throws Exception {
        UUID athleteId = UUID.randomUUID();
        when(athleteService.updateAthlete(eq(athleteId), any(Athlete.class)))
                .thenThrow(new AthleteNotFoundException("No Athlete found with id: " + athleteId));

        mockMvc.perform(put("/api/athletes/{athleteId}", athleteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Sidney",
                                  "lastName": "Crosby",
                                  "position": "C",
                                  "shoots": "L",
                                  "number": 87
                                }"""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void getAthlete_invalidUuid_returns400() throws Exception {
        mockMvc.perform(get("/api/athletes/{athleteId}", "b2c2751ba48n"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.parameter").value("athleteId"))
                .andExpect(jsonPath("$.expectedType").value("UUID"));
    }

    @Test
    void createAthlete_validRequest_returns201() throws Exception {
        Athlete created = new Athlete("Sidney", "Crosby", Position.C, Shoots.L, 87);
        when(athleteService.createAthlete(any(Athlete.class))).thenReturn(created);

        mockMvc.perform(post("/api/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Sidney",
                                  "lastName": "Crosby",
                                  "position": "C",
                                  "shoots": "L",
                                  "number": 87
                                }"""))
                .andExpect(status().isCreated());
    }
}
