package com.interview.service;

import com.interview.model.Account;
import com.interview.model.AuditLog;
import com.interview.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Log an account action (CREATE, UPDATE, DELETE)
     * Uses REQUIRES_NEW to ensure audit log is saved even if parent transaction rolls back
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAccountAction(UUID accountId, String action, String details) {
        String performedBy = getCurrentUsername();
        AuditLog auditLog = new AuditLog(accountId, action, details, performedBy);
        auditLogRepository.save(auditLog);
    }

    /**
     * Log account creation
     */
    public void logAccountCreation(Account account) {
        String details = String.format("Account created: %s %s (%s)",
                account.getFirstName(), account.getLastName(), account.getEmail());
        logAccountAction(account.getId(), "CREATE", details);
    }

    /**
     * Log account update
     */
    public void logAccountUpdate(Account account) {
        String details = String.format("Account updated: %s %s (%s)",
                account.getFirstName(), account.getLastName(), account.getEmail());
        logAccountAction(account.getId(), "UPDATE", details);
    }

    /**
     * Log account deletion (soft delete)
     */
    public void logAccountDeletion(Account account) {
        String details = String.format("Account deleted: %s %s (%s)",
                account.getFirstName(), account.getLastName(), account.getEmail());
        logAccountAction(account.getId(), "DELETE", details);
    }
    /**
     * Get current authenticated user's username (email)
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // If there's any error getting the current user, just return "SYSTEM"
        }
        return "SYSTEM";
    }
}
