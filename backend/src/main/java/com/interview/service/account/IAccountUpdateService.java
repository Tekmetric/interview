package com.interview.service.account;

import com.interview.dto.account.request.AccountUpdateRequestDTO;
import com.interview.dto.account.response.AccountUpdateResponseDTO;

/**
 * Service interface for account update operations.
 */
public interface IAccountUpdateService {
    
    AccountUpdateResponseDTO updateAccount(String accountId, AccountUpdateRequestDTO request);
    
    AccountUpdateResponseDTO patchAccount(String accountId, AccountUpdateRequestDTO request);
}

