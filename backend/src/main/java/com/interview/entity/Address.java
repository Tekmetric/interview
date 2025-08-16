package com.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "street", nullable = false, length = 255)
    private String street;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "zip", nullable = false, length = 20)
    private String zip;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    private Customer customer;

    // Without @Builder.Default, version would be null because the builder ignores the field's inline initialization.
    @Column(name = "version", nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    @Builder.Default
    private Integer version = 1;

    // updatable = false: Hibernate ignores this field in the UPDATE SQL.
    // @CreationTimestamp: the AddressMapper leaves createdAt filed as null, which cause the SQL insert to explicit set
    // createdAt as null in DB and will not trigger the default date creation logic in
    // DB. The DB default takes effect only if createdAt field is omitted in SQL insert.
    // So on the JPA level, use @CreationTimestamp to create the timestamp and insert into the DB.
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
