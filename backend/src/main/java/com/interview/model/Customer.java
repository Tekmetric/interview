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
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
