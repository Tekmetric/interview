package com.interview.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.interview.models.bank.Bank;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;

    @Column(unique = true)
    private String username;

    // Stores HMAC-SHA256 of the password, Base64-encoded
    @JsonIgnore
    private String password;

    // Per-user random key used for HMAC, Base64-encoded
    @JsonIgnore
    private String passwordKey;

    private LocalDate dateOfBirth;
    @JsonIgnore
    private String ssn;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Bank> banks = new ArrayList<>();

    private Gender gender;
    private String email;
    private String phoneNumber;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
