package com.interview.service.account;

import com.interview.dto.account.response.AccountDeleteResponseDTO;

/**
 * Service interface for account deletion operations.
 */
public interface IAccountDeleteService {
    
    AccountDeleteResponseDTO deleteAccount(String accountId);
}

