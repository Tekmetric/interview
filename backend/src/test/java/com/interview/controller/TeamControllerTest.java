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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        MvcResult result = mockMvc.perform(get("/v1/teams"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TeamDto> teams = toTeamDtoList(result);

        assertEquals(5, teams.size());
        assertTeamDtoFields(teams.get(0),1L, "Red Sox", "Boston", 1, 2);
        assertTeamDtoFields(teams.get(1),2L, "Yankees", "New York City", 3, 4);
        assertTeamDtoFields(teams.get(2),3L, "Orioles", "Baltimore", 5, 6);
        assertTeamDtoFields(teams.get(3),4L, "Rays", "Tampa Bay", 7, 8);
        assertTeamDtoFields(teams.get(4),5L, "Blue Jays", "Toronto", 9, 10);
    }

    @Test
    @Order(2)
    public void getsTeamById() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/teams/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTeamDtoFields(toTeamDto(result),1L, "Red Sox", "Boston", 1, 2);
    }

    @Test
    @Order(3)
    public void checksThatTeamExistsBeforeGettingById() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/teams/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(4)
    public void createsTeam() throws Exception {
        TeamDto team = new TeamDto();
        team.setName("Nationals");
        team.setCity("Washington");
        team.setNumWins(11);
        team.setNumLosses(12);

        MvcResult result = mockMvc.perform(post("/v1/teams").contentType(MediaType.APPLICATION_JSON).content(toJsonString(team)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTeamDtoFields(toTeamDto(result), 6L, "Nationals", "Washington", 11, 12);
    }

    @Test
    @Order(5)
    public void validatesCreateTeamRequestBody() throws Exception {
        mockMvc.perform(post("/v1/teams").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    public void deletesTeamById() throws Exception {
        mockMvc.perform(get("/v1/teams/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/v1/teams/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/teams/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    public void deletesTeamEvenIfItDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/teams/99"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/v1/teams/99"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(8)
    public void updatesTeamById() throws Exception {
        TeamDto team = new TeamDto();
        team.setNumWins(100);

        MvcResult before = mockMvc.perform(get("/v1/teams/2"))
                .andExpect(status().isOk())
                .andReturn();
        TeamDto beforeDto = toTeamDto(before);

        mockMvc.perform(patch("/v1/teams/2").contentType(MediaType.APPLICATION_JSON).content(toJsonString(team)))
                .andDo(print())
                .andExpect(status().isNoContent());

        MvcResult after = mockMvc.perform(get("/v1/teams/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTeamDtoFields(
                toTeamDto(after),
                beforeDto.getId(),
                beforeDto.getName(),
                beforeDto.getCity(),
                100,
                beforeDto.getNumLosses()
        );
    }

    @Test
    @Order(9)
    public void checksThatTeamExistsBeforeUpdatingById() throws Exception {
        TeamDto team = new TeamDto();
        team.setNumWins(100);

        mockMvc.perform(patch("/v1/teams/99").contentType(MediaType.APPLICATION_JSON).content(toJsonString(team)))
                .andDo(print())
                .andExpect(status().isNotFound());
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
