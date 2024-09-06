package com.interview.model;

import com.interview.dto.TeamDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="TEAMS")
public class Team extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private int numWins;

    @Column(nullable = false)
    private int numLosses;

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

    public int getNumWins() {
        return numWins;
    }

    public void setNumWins(int numWins) {
        this.numWins = numWins;
    }

    public int getNumLosses() {
        return numLosses;
    }

    public void setNumLosses(int numLosses) {
        this.numLosses = numLosses;
    }

    public TeamDto toDto() {
        TeamDto retVal = new TeamDto();
        retVal.setId(getId());
        retVal.setName(getName());
        retVal.setCity(getCity());
        retVal.setNumWins(getNumWins());
        retVal.setNumLosses(getNumLosses());
        return retVal;
    }
}
