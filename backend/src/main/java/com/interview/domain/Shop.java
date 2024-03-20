package com.interview.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "shop")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name cannot be shorter than 3 chars and longer than 100")
    private String name;

    @NotBlank(message = "Location is mandatory")
    private String location;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Invoice> invoices;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Supplier> suppliers;
}
