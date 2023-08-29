package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private Long id;
    private String name;
    private Integer rank;
    private String birthdate;
    private String birthplace;
    private String turnedPro;
    private Double weight;
    private Double height;
    private String coach;
    private StatsDto stats;
    private List<ScoreDto> previousResults;
    private List<TournamentDto> tournaments;
    private List<RacquetDto> racquets;
}

