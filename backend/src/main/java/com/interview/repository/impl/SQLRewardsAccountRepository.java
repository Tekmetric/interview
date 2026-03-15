package com.interview.repository.impl;

import com.interview.error.NotFoundException;
import com.interview.model.domain.RewardsAccount;
import com.interview.repository.RewardsAccountRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SQLRewardsAccountRepository implements RewardsAccountRepository {
    
    private final EntityManager entityManager;

    @Autowired
    public SQLRewardsAccountRepository(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public UUID createRewardsAccount(){
        RewardsAccount rewardsAccount = new RewardsAccount();
        entityManager.persist(rewardsAccount);
        return rewardsAccount.getId();
    }

    @Override
    public float getRewardsAccountBalance(UUID rewardsAccountId){
        return Optional.ofNullable(entityManager.find(RewardsAccount.class, rewardsAccountId)).map(RewardsAccount::getBalance).orElseThrow(() -> new NotFoundException("Rewards account not found!"));
    }

    @Override
    public void updateRewardsAccountBalance(UUID rewardsAccountId, float newBalance) {
        RewardsAccount rewardsAccount = Optional.ofNullable(entityManager.find(RewardsAccount.class, rewardsAccountId)).orElseThrow(() -> new NotFoundException("Rewards account not found when attempting to update account balance!"));
        rewardsAccount.setBalance(newBalance);
    }
}
