package com.interview.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import com.interview.persistence.enums.ApplicationStatus;
import com.interview.persistence.enums.LoanPurpose;

@Entity
@Table(
    name = "credit_application",
    indexes = {
        @Index(name = "idx_credit_app_customer_id",         columnList = "customer_id"),
        @Index(name = "idx_credit_app_status",              columnList = "status"),
        @Index(name = "idx_credit_app_customer_status",     columnList = "customer_id, status"),
        @Index(name = "idx_credit_app_submitted_at",        columnList = "submitted_at")
    }
)
@Getter
@Setter
public class CreditApplication extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "customer_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_credit_app_customer_id")
    )
    private Customer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @NotNull
    @Positive
    @Column(name = "requested_loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedLoanAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_purpose", nullable = false, length = 30)
    private LoanPurpose loanPurpose;

    @NotNull
    @PositiveOrZero
    @Column(name = "monthly_debt", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyDebt;

    @Size(max = 1000)
    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    private List<SupportingDocument> documents = new ArrayList<>();

    @PrePersist
    protected void onSubmit() {
        this.submittedAt = Instant.now();
    }
}
