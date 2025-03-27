package com.interview.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the Customer entity in the database.
 * The data model could include a variety of other attributes, such as first/last name, phone number address, as well
 * as relations to other tables (eg orders).
 */
@Entity
@Table(name = "customer", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}), @UniqueConstraint(columnNames = "email")})
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column
    private String name;
    @Column
    private String address;

}
