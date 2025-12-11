package com.interview.exception;

/**
 * Exception thrown when an account is not found.
 */
public class AccountNotFoundException extends RuntimeException {
    
    private final Long accountId;
    private final String accountReferenceId;
    
    public AccountNotFoundException(String message) {
        super(message);
        this.accountId = null;
        this.accountReferenceId = null;
    }
    
    public AccountNotFoundException(Long accountId) {
        super(String.valueOf(accountId));
        this.accountId = accountId;
        this.accountReferenceId = null;
    }
    
    public AccountNotFoundException(String accountReferenceId, boolean isReferenceId) {
        super(accountReferenceId);
        this.accountId = null;
        this.accountReferenceId = accountReferenceId;
    }
    
    public Long getAccountId() {
        return accountId;
    }
    
    public String getAccountReferenceId() {
        return accountReferenceId;
    }
}

