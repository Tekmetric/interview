package com.interview.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Table(name = "TOURNAMENT_TYPE")
@Entity
public class TournamentType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME", unique = true)
    @NotNull
    @NotBlank(message = "Tournament type name is mandatory")
    private String name;
}
