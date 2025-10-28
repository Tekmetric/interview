package com.interview.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id of Team", example = "2")
    private Long id;

    @NotBlank
    @Schema(description = "Name of Team", example = "Team 1")
    private String name;
    @Schema(description = "Players of Team", example = "Kevin Hall, Ben Jones")
    private String players;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "league_id")
    @JsonBackReference
    @Schema(description = "LeagueId associated with Team", example = "3")
    private League league;

    @Override
    public String toString() {
        Long leagueId = null;
        if (league != null) {
            leagueId = league.getId();
        }
        return "{name: " + name + ", players: " + players + ", leagueId: " + leagueId + "}";
    }
}
