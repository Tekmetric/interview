package com.interview.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Optional<UUID> getCustomerRewardsAccount(UUID customerId);
    void updateCustomerRewardsAccount(UUID customerId, UUID rewardsAccount);
    List<UUID> getActiveUsersForRewardsAccount(UUID rewardsAccountId);
}

