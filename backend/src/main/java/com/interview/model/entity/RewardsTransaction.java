package com.interview.model.entity;

import com.interview.model.RewardsTransactionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table
@Data
public class RewardsTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private UUID id;

    @CreationTimestamp
    @Column(name="created", nullable = false, updatable = false)
    private ZonedDateTime created;

    @ManyToOne
    @JoinColumn(name="rewards_account", referencedColumnName = "id")
    private RewardsAccount rewardsAccount;

    @Column(name="transaction_amount", nullable = false)
    private float transactionAmount;

    @Column(name="transaction_type", nullable = false)
    private RewardsTransactionType rewardsTransactionType;

    @Column(name="purchase_id")
    private UUID purchaseId;

    @Column(name="rewards_rate")
    private float rewardsRate;

    public RewardsTransaction(RewardsAccount rewardsAccount, float transactionAmount,RewardsTransactionType type, float rewardsRate, UUID purchaseId){
        this.rewardsAccount = rewardsAccount;
        this.transactionAmount = transactionAmount;
        this.rewardsTransactionType = type;
        this.rewardsRate = rewardsRate;
        this.purchaseId = purchaseId;
    }
}
