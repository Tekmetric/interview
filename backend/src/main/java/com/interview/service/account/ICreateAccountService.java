package com.interview.service.account;

import com.interview.dto.account.request.AccountCreateRequestDTO;
import com.interview.dto.account.response.AccountCreateResponseDTO;

/**
 * Service interface for account creation operations.
 */
public interface ICreateAccountService {
    
    AccountCreateResponseDTO createAccount(AccountCreateRequestDTO request);
}

