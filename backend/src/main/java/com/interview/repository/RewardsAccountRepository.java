package com.interview.repository;

import java.util.UUID;

public interface RewardsAccountRepository {
    UUID createRewardsAccount();
    float getRewardsAccountBalance(UUID rewardsAccountId);
    void updateRewardsAccountBalance(UUID rewardsAccountId, float newBalance);
}
