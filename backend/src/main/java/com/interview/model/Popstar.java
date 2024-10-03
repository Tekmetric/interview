package com.interview.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@lombok.Data
@Entity(name = "popstar")
@Table(name = "popstars")
public class Popstar {

    @Id
    @Column(name ="id")
    private Long id;

    @Column(name ="firstname", nullable = false)
    private String firstName;

    @Column(name ="lastname", nullable = false)
    private String lastName;

    @Column(name ="born", nullable = false)
    private LocalDate born;

    @Column(name ="died", nullable = true)
    private LocalDate died;
}
