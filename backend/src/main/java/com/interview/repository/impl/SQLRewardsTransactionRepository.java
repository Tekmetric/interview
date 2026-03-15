package com.interview.repository.impl;

import com.interview.model.domain.RewardsAccount;
import com.interview.model.domain.RewardsTransaction;
import com.interview.model.domain.RewardsTransactionType;
import com.interview.repository.RewardsTransactionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SQLRewardsTransactionRepository implements RewardsTransactionRepository {

    private final EntityManager entityManager;

    @Autowired
    public SQLRewardsTransactionRepository(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public UUID postTransaction(UUID rewardsAccountId, float transactionAmount, RewardsTransactionType type, float rewardsRate, UUID purchaseId) {
        RewardsTransaction transaction = new RewardsTransaction(entityManager.getReference(RewardsAccount.class, rewardsAccountId), transactionAmount, type, rewardsRate, purchaseId);
        entityManager.persist(transaction);
        return transaction.getId();
    }

    // TODO: placeholder impl, connect this to db
    public List<UUID> getTransactionsByRewardsAccount(){
        return new ArrayList<>();
    }
}
