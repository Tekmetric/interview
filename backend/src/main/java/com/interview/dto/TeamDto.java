package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TeamDto extends BaseDto {

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotNull
    private Integer numWins;

    @NotNull
    private Integer numLosses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getNumWins() {
        return numWins;
    }

    public void setNumWins(Integer numWins) {
        this.numWins = numWins;
    }

    public Integer getNumLosses() {
        return numLosses;
    }

    public void setNumLosses(Integer numLosses) {
        this.numLosses = numLosses;
    }
}
