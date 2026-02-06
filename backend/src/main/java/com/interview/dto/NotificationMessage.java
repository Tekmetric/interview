package com.interview.dto;

import java.time.LocalDateTime;

/**
 * DTO for entity change notifications.
 *
 * Sent via both WebSocket and JMS when entities are created, updated, or deleted.
 */
public class NotificationMessage {
    private NotificationAction action;
    private EntityType entityType;
    private Long entityId;
    private LocalDateTime timestamp;

    public NotificationMessage() {
    }

    public NotificationMessage(NotificationAction action, EntityType entityType, Long entityId) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = LocalDateTime.now();
    }

    public NotificationAction getAction() {
        return action;
    }

    public void setAction(NotificationAction action) {
        this.action = action;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
