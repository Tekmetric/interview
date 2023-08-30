package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.interview.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto {
    private Long id;
    private Integer points;
    private String opponentName;
    private Integer opponentPoints;
    @JsonIgnore
    private Player player;
}

