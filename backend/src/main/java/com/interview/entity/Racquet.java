package com.interview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Table(name = "RACQUET",
    uniqueConstraints = {@UniqueConstraint(name = "UniqueBrandAndModel", columnNames = {"BRAND", "MODEL"})})
@Entity
public class Racquet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "BRAND")
    @NotNull
    @NotBlank(message = "Racquet Brand is mandatory")
    private String brand;
    @NotNull
    @NotBlank(message = "Racquet Model is mandatory")
    @Column(name = "MODEL")
    private String model;
    @Column(name = "WEIGHT")
    private Integer weight;
    @Column(name = "HEAD_SIZE")
    private Integer headSize;
}