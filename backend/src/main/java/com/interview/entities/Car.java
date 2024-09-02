package com.interview.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "car")
@Table(name = "cars")
@Data
public class Car {
    @Id
    @GeneratedValue
    private Long id;

    private String releaseYear;

    private String make;

    private String model;

    private String licensePlateNumber;

    private Long customerId;
}
