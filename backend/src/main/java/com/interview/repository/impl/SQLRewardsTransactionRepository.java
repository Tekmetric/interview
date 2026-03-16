package com.interview.repository.impl;

import com.interview.model.RewardsTransactionSummary;
import com.interview.model.RewardsTransactionType;
import com.interview.model.entity.RewardsAccount;
import com.interview.model.entity.RewardsTransaction;
import com.interview.repository.RewardsTransactionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public List<RewardsTransactionSummary> getTransactionsByRewardsAccount(UUID rewardsAccountId){
        String transactionsQuery = "SELECT new RewardsTransactionSummary(t.created, t.rewardsTransactionType, t.transactionAmount, t.rewardsRate) FROM RewardsTransaction t WHERE rewardsAccount.id = :id";
        // TODO: handle account not found
        // TODO: handle pagination here, or at least make the max configurable
        return entityManager.createQuery(transactionsQuery, RewardsTransactionSummary.class).setParameter("id", rewardsAccountId).setMaxResults(15).getResultList();
    }
}
