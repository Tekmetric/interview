package com.interview.service;

import com.interview.error.NotFoundException;
import com.interview.model.request.EnrollCustomerRequest;
import com.interview.model.request.GetCustomerRewardsAccountRequest;
import com.interview.model.request.UnenrollCustomerRequest;
import com.interview.repository.CustomerRepository;
import com.interview.repository.RewardsAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
            logger.info("Existing rewards account {} found for customer {}. A new rewards account will not be created.", rewardsAccount, request.getCustomerId());
            return rewardsAccount;
        }).orElseGet(() -> createOrLinkRewardsAccount(request.getCustomerId(), request.getRewardsAccountId()));
    }

    @Transactional
    public UUID unenrollCustomer(UnenrollCustomerRequest request){
        UUID rewardsAccountId = customerRepository.getCustomerRewardsAccount(request.getCustomerId()).orElseThrow(() -> new NotFoundException("No rewards account found to unenroll for given customer"));
        List<UUID> activeUsers = customerRepository.getActiveUsersForRewardsAccount(rewardsAccountId);
        customerRepository.updateCustomerRewardsAccount(request.getCustomerId(), null);
        if(activeUsers.size() == 1){
            logger.info("Rewards account has no other active users. Account will be deleted.");
            rewardsAccountRepository.deleteRewardsAccount(rewardsAccountId);
        } else {
            logger.info("Rewards account has {} active users. Account will not be deleted. Users: {}", activeUsers.size(), activeUsers);
        }
        return rewardsAccountId;
    }

    private UUID createOrLinkRewardsAccount(UUID customerId, UUID rewardsAccountId){
        UUID rewardsAccountIdToLink = Optional.ofNullable(rewardsAccountId).orElseGet(() -> {
            logger.info("No existing rewards account provided to link, a new one will be created.");
            return rewardsAccountRepository.createRewardsAccount();
        });
        customerRepository.updateCustomerRewardsAccount(customerId, rewardsAccountIdToLink);
        return rewardsAccountIdToLink;
    }

}
