package com.interview.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "league")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id of League", example = "1")
    private Long id;

    @NotBlank
    @Schema(description = "Name of League", example = "League 1")
    private String name;
    @NotBlank
    @Schema(description = "Location of league", example = "Richmond, VA")
    private String location;
    @NotBlank
    @Schema(description = "Skill level of league", example = "Competitive")
    private String skillLevel;
    @OneToMany(mappedBy = "league", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    @Schema(description = "The teams associated with the League", example = "[{id: 1, name: Team 1, players: Kevin Hall, Another User, leagueId: 1}]")
    private List<Team> teams;

    public League(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder teamString = new StringBuilder();
        if (teams != null && !teams.isEmpty()) {
            teamString.append(teams.getFirst().toString());
            for (int i = 1; i< teams.size(); i++) {
                teamString.append(", ").append(teams.get(i).toString());
            }
        }
        return "{id: " + id +
                ", name: " + name +
                ", location: " + location +
                ", skillLevel: " + skillLevel +
                ", teams: [" + teamString+ "]}";
    }
}
