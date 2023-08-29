package com.interview.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "PLAYER")
@Entity
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;
  @Column(name = "NAME", unique = true)
  @NotBlank(message = "Name is mandatory")
  private String name;
  @Column(name = "RANK")
  @NotNull
  private Integer rank;
  @Column(name = "BIRTHDATE")
  @NotNull
  private LocalDate birthdate;
  @Column(name = "BIRTHPLACE")
  @NotNull
  private String birthplace;
  @Column(name = "TURNED_PRO")
  @NotNull
  private LocalDate turnedPro;
  @Column(name = "WEIGHT")
  @NotNull
  private Double weight;
  @Column(name = "HEIGHT")
  private Double height;
  @NotNull
  @Column(name = "COACH")
  @Nullable
  private String coach;
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "STATS_ID")
  private Stats stats;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Score> previousResults;
  @ManyToMany(cascade = CascadeType.MERGE)
  @JoinTable(
    name = "PLAYER_TOURNAMENT",
    joinColumns = @JoinColumn(name = "PLAYER_ID"),
    inverseJoinColumns = @JoinColumn(name = "TOURNAMENT_ID"))
  private List<Tournament> tournaments = new ArrayList<>();
  @ManyToMany(cascade = CascadeType.MERGE)
  @JoinTable(
    name = "PLAYER_RACQUET",
    joinColumns = @JoinColumn(name = "PLAYER_ID"),
    inverseJoinColumns = @JoinColumn(name = "RACQUET_ID"))
  private List<Racquet> racquets = new ArrayList<>();
}

