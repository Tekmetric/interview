package com.interview.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Table(name = "SCORE")
@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "POINTS")
    @NotNull
    private Integer points;
    @Column(name = "OPPONENT_NAME")
    @NotNull
    private String opponentName;
    @Column(name = "OPPONENT_POINTS")
    @NotNull
    private Integer opponentPoints;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Player player;
}
