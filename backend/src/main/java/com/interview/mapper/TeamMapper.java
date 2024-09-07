package com.interview.mapper;

import com.interview.dto.TeamDto;
import com.interview.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public TeamDto toTeamDto(Team team) {
        TeamDto retVal = new TeamDto();
        retVal.setId(team.getId());
        retVal.setName(team.getName());
        retVal.setCity(team.getCity());
        retVal.setNumWins(team.getNumWins());
        retVal.setNumLosses(team.getNumLosses());
        return retVal;
    }

    public Team toTeam(TeamDto teamDto) {
        Team retVal = new Team();
        retVal.setName(teamDto.getName());
        retVal.setCity(teamDto.getCity());
        retVal.setNumWins(teamDto.getNumWins());
        retVal.setNumLosses(teamDto.getNumLosses());
        return retVal;
    }
}
