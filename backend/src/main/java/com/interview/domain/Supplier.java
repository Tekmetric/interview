package com.interview.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "supplier")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private SupplierType supplierType;

    @ManyToMany(mappedBy = "suppliers")
    private Set<Shop> shops;
}
