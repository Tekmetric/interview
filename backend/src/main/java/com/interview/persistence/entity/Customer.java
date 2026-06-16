package com.interview.persistence.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import com.interview.persistence.entity.embedded.Address;
import com.interview.persistence.entity.embedded.EmploymentDetails;
import com.interview.validation.ValidAdultAge;
import com.interview.validation.ValidPhone;
import com.interview.validation.ValidSSN;

/**
 * SSN is stored as plain text for this demo. In production, encrypt at rest
 * via AWS KMS before writing and decrypt on read.
 */
@Entity
@Table(
    name = "customer",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_customer_email", columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_customer_last_name",         columnList = "last_name"),
        @Index(name = "idx_customer_employment_status", columnList = "employment_status"),
        @Index(name = "idx_customer_date_created",      columnList = "date_created")
    }
)
@Getter
@Setter
public class Customer extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @ValidPhone
    @Column(name = "phone", length = 20)
    private String phone;

    @NotNull
    @ValidAdultAge
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank
    @ValidSSN
    @Column(name = "ssn", nullable = false, length = 11)
    private String ssn;

    @Valid
    @NotNull
    @Embedded
    private Address address;

    @Valid
    @NotNull
    @Embedded
    private EmploymentDetails employmentDetails;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CreditApplication> creditApplications = new ArrayList<>();
}
