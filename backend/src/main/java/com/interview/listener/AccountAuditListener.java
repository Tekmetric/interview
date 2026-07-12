package com.interview.listener;

import com.interview.config.SpringContext;
import com.interview.model.Account;
import com.interview.service.AuditLogService;
import jakarta.persistence.*;

/**
 * JPA Entity Listener for Account auditing
 * Automatically logs CREATE, UPDATE, and DELETE (soft delete) actions
 *
 */
public class AccountAuditListener {

    private static AuditLogService auditLogService;

    private static AuditLogService getAuditService() {
        if (auditLogService == null) {
            auditLogService = SpringContext.getBean(AuditLogService.class);
        }
        return auditLogService;
    }

    @PostPersist
    public void postPersist(Account account) {
        try {
            getAuditService().logAccountCreation(account);
        } catch (Exception e) {
            System.err.println("Failed to log account creation audit: " + e.getMessage());
        }
    }

    @PostUpdate
    public void postUpdate(Account account) {
        try {
            // Since we prevent updates to already-deleted accounts in AccountService,
            // if deletedAt is not null here, it means this update was the soft delete operation
            if (account.getDeletedAt() != null && !account.isAuditedAsDeleted()) {
                // Mark as audited to avoid logging multiple times
                account.setAuditedAsDeleted(true);
                getAuditService().logAccountDeletion(account);
            } else if (account.getDeletedAt() == null) {
                // Regular update
                getAuditService().logAccountUpdate(account);
            }
        } catch (Exception e) {
            System.err.println("Failed to log account update/delete audit: " + e.getMessage());
        }
    }
}
