package com.interview.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.TeamDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    public void getsAllTeams() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/teams").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TeamDto> teams = toTeamDtoList(result);

        assertEquals(5, teams.size());
    }

    @Test
    @Order(2)
    public void getsTeamById() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/teams/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTeamDtoFields(toTeamDto(result),1L, "Red Sox", "Boston", 1, 2);
    }

    @Test
    @Order(3)
    public void throwsNotFoundForInvalidId() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/teams/99").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(4)
    public void createsTeams() throws Exception {
        TeamDto team = new TeamDto();
        team.setName("Nationals");
        team.setCity("Washington");
        team.setNumWins(11);
        team.setNumLosses(12);

        MvcResult result = mockMvc.perform(post("/v1/teams").contentType(MediaType.APPLICATION_JSON).content(toJsonString(team)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTeamDtoFields(toTeamDto(result),6L, "Nationals", "Washington", 11, 12);
    }

    @Test
    @Order(5)
    public void validatesCreateRequestBody() throws Exception {
        mockMvc.perform(post("/v1/teams").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void assertTeamDtoFields(TeamDto actual, Long id, String name, String city, int numWins, int numLosses) {
        assertEquals(id, actual.getId());
        assertEquals(name, actual.getName());
        assertEquals(city, actual.getCity());
        assertEquals(numWins, actual.getNumWins());
        assertEquals(numLosses, actual.getNumLosses());
    }

    private TeamDto toTeamDto(MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), TeamDto.class);
    }

    private List<TeamDto> toTeamDtoList(MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readerForListOf(TeamDto.class).readValue(result.getResponse().getContentAsString());
    }

    private String toJsonString(TeamDto team) throws JsonProcessingException {
        return objectMapper.writeValueAsString(team);
    }
}
