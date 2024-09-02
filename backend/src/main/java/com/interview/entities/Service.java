package com.interview.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "service")
@Table(name = "services")
@Data
public class Service {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String cost;
}
