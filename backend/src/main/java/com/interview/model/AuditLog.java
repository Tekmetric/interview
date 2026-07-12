package com.interview.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "account_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID accountId;

    @Column(name = "action", nullable = false, length = 20)
    private String action; // CREATE, UPDATE, DELETE

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details; // JSON or text description of changes

    @Column(name = "performed_by")
    private String performedBy; // Email of user who performed the action

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // Constructors
    public AuditLog() {
    }

    public AuditLog(UUID accountId, String action, String details, String performedBy) {
        this.accountId = accountId;
        this.action = action;
        this.details = details;
        this.performedBy = performedBy;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                ", details='" + details + '\'' +
                ", performedBy='" + performedBy + '\'' +
                '}';
    }
}
