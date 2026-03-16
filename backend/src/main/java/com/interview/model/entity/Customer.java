package com.interview.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"first_name", "last_name", "email"})})
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private UUID id;

    @Column(name="first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name="last_name", nullable = false, length = 50)
    private String lastName;

    @CreationTimestamp
    @Column(name="created", nullable = false, updatable = false)
    private ZonedDateTime created;

    @ManyToOne
    @JoinColumn(name="rewards_account", referencedColumnName = "id")
    private RewardsAccount rewardsAccount;

    @Column(name="email", nullable=false, length = 50)
    private String email;



}
