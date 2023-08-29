package com.interview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@Table(name = "TOURNAMENT")
@Entity
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME", unique = true)
    @NotNull
    @NotBlank(message = "Tournament name is mandatory")
    private String name;
    @NotNull
    @NotBlank(message = "City is mandatory")
    @Column(name = "CITY")
    private String city;
    @NotNull
    @NotBlank(message = "Country is mandatory")
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "PRIZE_MONEY")
    @NotNull
    private Double prizeMoney;
    @Column(name = "DATE")
    @NotNull
    private LocalDate date;
    @JoinColumn(name = "SURFACE_ID")
    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    private Surface surface;
}
