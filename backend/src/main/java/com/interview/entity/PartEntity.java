package com.interview.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "parts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
public class PartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false)
    private int inventory;
}
