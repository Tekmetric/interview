package com.interview.Entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "olympicteams")
public class OlympicTeam {

    @Id
    @Generated(GenerationTime.INSERT)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public Integer id;

    @Column(name = "team_country", nullable = false, unique = true)
    public String teamCountry;

    @Column(name = "total_athletes", nullable = false)
    public Integer totalAthletes;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME)
    public LocalDateTime createdAt;

    @Column(name = "updated_on", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME)
    public LocalDateTime updatedOn;

    @Column(name = "disabled_on", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME)
    public LocalDateTime disabledOn;

    public OlympicTeam() {
    }

    public Integer getId() {
        return id;
    }

    public String getTeamCountry() {
        return teamCountry;
    }

    public void setTeamCountry(String teamCountry) {
        this.teamCountry = teamCountry;
    }

    public Integer getTotalAthletes() {
        return totalAthletes;
    }

    public void setTotalAthletes(Integer totalAthletes) {
        this.totalAthletes = totalAthletes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public LocalDateTime getDisabledOn() {
        return disabledOn;
    }

    public void setDisabledOn(LocalDateTime disabledOn) {
        this.disabledOn = disabledOn;
    }

    public static OlympicTeam fuzzOlympicTeam() {
        OlympicTeam user = new OlympicTeam();
        user.totalAthletes = 123;
        user.teamCountry = "Spain";
        // everything else is either auto-generated or nullable
        return user;
    }

}
