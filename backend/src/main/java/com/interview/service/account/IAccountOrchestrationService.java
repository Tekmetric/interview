package com.interview.service.account;

import com.interview.dto.account.request.AccountCreateRequestDTO;
import com.interview.dto.account.request.AccountListRequestDTO;
import com.interview.dto.account.request.AccountUpdateRequestDTO;
import com.interview.dto.account.response.AccountCreateResponseDTO;
import com.interview.dto.account.response.AccountDeleteResponseDTO;
import com.interview.dto.account.response.AccountDetailsResponseDTO;
import com.interview.dto.account.response.AccountListResponseDTO;
import com.interview.dto.account.response.AccountUpdateResponseDTO;

/**
 * Orchestrator service interface for account operations.
 */
public interface IAccountOrchestrationService {
    
    AccountCreateResponseDTO createAccount(AccountCreateRequestDTO request);
    
    AccountUpdateResponseDTO updateAccount(String accountId, AccountUpdateRequestDTO request);
    
    AccountDetailsResponseDTO getAccount(String accountId);
    
    AccountListResponseDTO getAccounts(AccountListRequestDTO request);
    
    AccountUpdateResponseDTO patchAccount(String accountId, AccountUpdateRequestDTO request);
    
    AccountDeleteResponseDTO deleteAccount(String accountId);
}

