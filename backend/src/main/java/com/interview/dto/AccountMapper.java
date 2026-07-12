package com.interview.dto;

import com.interview.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }

        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setFirstName(account.getFirstName());
        response.setLastName(account.getLastName());
        response.setEmail(account.getEmail());
        response.setPhone(account.getPhone());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());

        return response;
    }

    public Account toEntity(AccountRequest request) {
        if (request == null) {
            return null;
        }

        Account account = new Account();
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setEmail(request.getEmail());
        account.setPassword(request.getPassword());
        account.setPhone(request.getPhone());

        return account;
    }

    public void updateEntityFromRequest(Account account, AccountRequest request) {
        if (account == null || request == null) {
            return;
        }

        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setEmail(request.getEmail());
        account.setPassword(request.getPassword());
        account.setPhone(request.getPhone());
    }
}