package com.interview.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table
@Data
public class RewardsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    @ColumnDefault("0.0")
    // maintain balance since transaction data will only be retained for limited time
    private float balance;
}
