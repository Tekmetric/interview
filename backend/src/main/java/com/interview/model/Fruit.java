package com.interview.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "fruits", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "supplier", "batch_number"}))
public class Fruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    // for simplicity, keeping all info in this entity
    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    private String color;
    private String category;
    private Boolean organic;
    private Integer quantity;

    @Column(name = "origin_country")
    private String originCountry;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "registration_date", columnDefinition = "TIMESTAMP(3)")
    private Instant registrationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "last_update_date", columnDefinition = "TIMESTAMP(3)")
    private Instant lastUpdateDate;
}
