package com.interview.service.account;

import com.interview.dto.account.request.AccountListRequestDTO;
import com.interview.dto.account.response.AccountDetailsResponseDTO;
import com.interview.dto.account.response.AccountListResponseDTO;

/**
 * Service interface for account retrieval operations.
 */
public interface IAccountRetrievalService {
    
    AccountDetailsResponseDTO getAccount(String accountId);
    
    AccountListResponseDTO getAccounts(AccountListRequestDTO request);
}

