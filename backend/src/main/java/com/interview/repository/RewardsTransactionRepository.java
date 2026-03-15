package com.interview.repository;

import com.interview.model.domain.RewardsTransactionType;

import java.util.List;
import java.util.UUID;

public interface RewardsTransactionRepository {
    UUID postTransaction(UUID rewardsAccountId, float transactionAmount, RewardsTransactionType type, float rewardsRate, UUID purchaseId);
    List<UUID> getTransactionsByRewardsAccount();
}
