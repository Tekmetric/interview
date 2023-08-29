package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDto {
    private Long id;
    private String name;
    private String city;
    private String country;
    private Double prizeMoney;
    private String date;
    private SurfaceDto surface;
    private TournamentTypeDto type;
}

