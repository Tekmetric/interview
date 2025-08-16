package com.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name", nullable = true, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    // TODO EXPLAIN: ConstraintViolationException, Hibernate validates entity constraints during persist
//    @Lowercase(message = "Email must be in lowercase")
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    // updatable = false: Hibernate ignores this field in the UPDATE SQL.
    // TODO EXPLAIN: insertable = false, Hibernate won’t insert or update this column — only DB default will work — and your entity won’t have the field populated until after a reload.
//    @Column(name = "created_at", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    // TODO EXPLAIN: Hibernate to handle the timestamp
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "version", nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    @Builder.Default
    private Integer version = 1;

    // Do not use cascade = CascadeType.REMOVE for cascade deletion on JPA level, which can cause N + 1 problem
    @OneToMany(mappedBy = "customer")
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    public void addAddress(Address address) {
        addresses.add(address);
        address.setCustomer(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setCustomer(null);
    }
}