package com.interview.repository.impl;

import com.interview.error.NotFoundException;
import com.interview.model.domain.Customer;
import com.interview.model.domain.RewardsAccount;
import com.interview.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SQLCustomerRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(SQLCustomerRepository.class);
    private final EntityManager entityManager;

    @Autowired
    public SQLCustomerRepository(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public Optional<UUID> getCustomerRewardsAccount(UUID customerId) {
        Customer customer = Optional.ofNullable(entityManager.find(Customer.class, customerId)).orElseThrow(() -> new NotFoundException("Customer not found when querying for rewards account!"));
        return Optional.ofNullable(customer.getRewardsAccount()).map(RewardsAccount::getId);
    }

    @Override
    public void updateCustomerRewardsAccount(UUID customerId, UUID rewardsAccountId){
        Customer customer = Optional.ofNullable(entityManager.find(Customer.class, customerId)).orElseThrow(() -> new NotFoundException("Customer not found when attempting to update with rewards account!"));
        customer.setRewardsAccount(entityManager.getReference(RewardsAccount.class, rewardsAccountId));
    }

}
