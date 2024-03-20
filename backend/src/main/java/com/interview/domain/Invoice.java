package com.interview.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoice")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Double amount;

    private boolean paid;

    @ManyToOne(fetch = FetchType.LAZY)
    private Shop shop;
}
