package com.interview.service;

import com.interview.model.domain.RewardsTransactionType;
import com.interview.model.request.PostTransactionRequest;
import com.interview.repository.RewardsAccountRepository;
import com.interview.repository.RewardsTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RewardsTransactionService {

    private final float rewardsRate;
    private final RewardsAccountRepository rewardsAccountRepository;
    private final RewardsTransactionRepository rewardsTransactionRepository;

    @Autowired
    public RewardsTransactionService(@Value("${rewards.rate}") float rewardsRate, RewardsAccountRepository rewardsAccountRepository, RewardsTransactionRepository rewardsTransactionRepository){
        this.rewardsRate = rewardsRate;
        this.rewardsAccountRepository = rewardsAccountRepository;
        this.rewardsTransactionRepository = rewardsTransactionRepository;
    }

    public List<UUID> getRedemptionActivity(){
        return rewardsTransactionRepository.getTransactionsByRewardsAccount();
    }

    @Transactional
    public UUID postTransaction(PostTransactionRequest request){
        float previousBalance = rewardsAccountRepository.getRewardsAccountBalance(request.getRewardsAccountId());
        float newBalance = calculateNewBalance(previousBalance, request.getAmount(), request.getType());
        UUID transactionId = rewardsTransactionRepository.postTransaction(request.getRewardsAccountId(), request.getAmount(), request.getType(), rewardsRate, request.getPurchaseId());
        rewardsAccountRepository.updateRewardsAccountBalance(request.getRewardsAccountId(), newBalance);
        return transactionId;
    }

    private float calculateNewBalance(float previousBalance, float transactionAmount, RewardsTransactionType transactionType){
        float total_rewards = 0;
        switch(transactionType) {
            case RewardsTransactionType.PURCHASE:
                total_rewards = transactionAmount * rewardsRate;
                break;
            case RewardsTransactionType.REDEMPTION:
                total_rewards = transactionAmount * -1;
                break;
            case RewardsTransactionType.REFUND:
                total_rewards = transactionAmount * rewardsRate * -1;
                break;
            default:
                break;
        }
        return previousBalance + total_rewards;
    }
}
