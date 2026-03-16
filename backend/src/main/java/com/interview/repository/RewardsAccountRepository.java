package com.interview.repository;

import java.util.UUID;

public interface RewardsAccountRepository {
    UUID createRewardsAccount();
    void deleteRewardsAccount(UUID rewardsAccountId);
    float getRewardsAccountBalance(UUID rewardsAccountId);
    void updateRewardsAccountBalance(UUID rewardsAccountId, float newBalance);
}
