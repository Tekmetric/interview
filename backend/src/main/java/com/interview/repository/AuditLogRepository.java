package com.interview.repository;

import com.interview.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    // Find all audit logs for a specific account
    List<AuditLog> findByAccountIdOrderByTimestampDesc(UUID accountId);

    // Find audit logs by action type
    List<AuditLog> findByActionOrderByTimestampDesc(String action);

    // Find audit logs by account and action
    List<AuditLog> findByAccountIdAndActionOrderByTimestampDesc(UUID accountId, String action);
}
