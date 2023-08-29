package com.interview.entity;

import jakarta.persistence.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "STATS")
@Entity
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ACES", columnDefinition = "integer default 0")
    private Integer aces;
    @Column(name = "DOUBLE_FAULTS", columnDefinition = "integer default 0")
    private Integer doubleFaults;
    @Column(name = "WINS", columnDefinition = "integer default 0")
    private Integer wins;
    @Column(name = "LOSSES", columnDefinition = "integer default 0")
    private Integer losses;
    @Column(name = "TOURNAMENTS_PLAYED", columnDefinition = "integer default 0")
    private Integer tournamentsPlayed;
    @OneToOne(mappedBy = "stats", fetch = FetchType.LAZY)
    private Player player;
}
