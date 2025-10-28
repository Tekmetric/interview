package com.interview.model.dto;

import com.interview.model.Team;
import lombok.Getter;
import lombok.Setter;

/**
 * The Data Transfer Object for TeamController responses.
 */
@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;
    private String players;
    private Long leagueId;

    /**
     * Creates a new TeamDTO based on {@link Team}
     * @param team the team to transform
     */
    public TeamDTO(Team team)  {
        this.id = team.getId();
        this.name = team.getName();
        this.players = team.getPlayers();
        if (team.getLeague() != null) {
            this.leagueId = team.getLeague().getId();
        }
    }
}
