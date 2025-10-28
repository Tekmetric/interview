package com.interview.model.dto;

import com.interview.model.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;
    private String players;
    private Long leagueId;

    public TeamDTO(Team team)  {
        this.id = team.getId();
        this.name = team.getName();
        this.players = team.getPlayers();
        if (team.getLeague() != null) {
            this.leagueId = team.getLeague().getId();
        }
    }
}
