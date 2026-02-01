package com.interview.event;

import com.interview.dto.EntityType;
import com.interview.dto.NotificationAction;

/**
 * Event representing a change to an entity (Artist, Song, or Album).
 *
 * This event is published when an entity is created, updated, or deleted.
 * Using Spring's ApplicationEventPublisher ensures that event listeners
 * can be transaction-aware via @TransactionalEventListener, preventing
 * notifications from being sent before the transaction commits.
 *
 * Rationale for Event-Driven Approach:
 * - Transaction Safety: Events are only processed after successful commit
 * - Decoupling: Services don't directly depend on NotificationService
 * - Extensibility: Additional listeners can be added without modifying services
 * - Rollback Protection: If transaction fails, no notifications are sent
 */
public class EntityChangeEvent {
    private final NotificationAction action;
    private final EntityType entityType;
    private final Long entityId;

    public EntityChangeEvent(NotificationAction action, EntityType entityType, Long entityId) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public NotificationAction getAction() {
        return action;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }
}
