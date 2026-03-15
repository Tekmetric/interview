package com.interview.service;

import com.interview.model.request.EnrollCustomerRequest;
import com.interview.model.request.GetCustomerRewardsAccountRequest;
import com.interview.repository.CustomerRepository;
import com.interview.repository.RewardsAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RewardsEnrollmentService {

    private final Logger logger = LoggerFactory.getLogger(RewardsEnrollmentService.class);
    private final CustomerRepository customerRepository;
    private final RewardsAccountRepository rewardsAccountRepository;

    @Autowired
    public RewardsEnrollmentService(CustomerRepository customerRepository, RewardsAccountRepository rewardsAccountRepository){
        this.customerRepository = customerRepository;
        this.rewardsAccountRepository = rewardsAccountRepository;
    }

    public String getCustomerRewardsAccount(GetCustomerRewardsAccountRequest request) {
        return customerRepository.getCustomerRewardsAccount(request.getCustomerId()).map(UUID::toString).orElse("Customer not enrolled for rewards");
    }

    @Transactional
    public UUID enrollCustomer(EnrollCustomerRequest request) {
        return customerRepository.getCustomerRewardsAccount(request.getCustomerId()).map(rewardsAccount -> {
            logger.info("Existing rewards account {} found for customer {}", rewardsAccount, request.getCustomerId());
            return rewardsAccount;
        }).orElseGet(() -> createRewardsAccount(request.getCustomerId()));
    }

    public UUID createRewardsAccount(UUID customerId){
        UUID newRewardsAccountId = rewardsAccountRepository.createRewardsAccount();
        customerRepository.updateCustomerRewardsAccount(customerId, newRewardsAccountId);
        return newRewardsAccountId;
    }

}
