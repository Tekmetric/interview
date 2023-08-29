package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDto {
    private Long id;
    private Integer aces;
    private Integer doubleFaults;
    private Integer wins;
    private Integer losses;
    private Integer tournamentsPlayed;
}

