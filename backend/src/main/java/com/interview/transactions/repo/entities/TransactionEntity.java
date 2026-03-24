package com.interview.transactions.repo.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.*;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA needs no‑arg, but you don’t want it public
@AllArgsConstructor
@ToString(exclude = {"createdAt", "updatedAt"})  // optional: avoid huge toString from DB fields
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
