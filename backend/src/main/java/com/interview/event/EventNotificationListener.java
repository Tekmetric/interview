package com.interview.event;

import com.interview.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener for entity change events that sends real-time notifications.
 *
 * This class uses @TransactionalEventListener to ensure notifications are only
 * sent AFTER the database transaction successfully commits. This prevents the
 * following problems:
 *
 * 1. Phantom Notifications: Without transaction awareness, if a service method
 *    sends a notification and then the transaction rolls back (due to validation
 *    error, constraint violation, or exception), clients would receive notifications
 *    about changes that never actually happened in the database.
 *
 * 2. Inconsistent State: Clients receiving notifications before commit could query
 *    the database and not find the entity that was just "created" or see stale data
 *    for "updated" entities.
 *
 * 3. Race Conditions: Early notifications could cause race conditions where clients
 *    act on incomplete or uncommitted data.
 *
 * By using TransactionPhase.AFTER_COMMIT, we guarantee:
 * - Notifications are only sent for successfully persisted changes
 * - Clients can immediately query and see the committed data
 * - System remains consistent even if transactions fail
 *
 * Example Scenario Prevented:
 * Without @TransactionalEventListener:
 *   1. Service calls notificationService.notify() immediately
 *   2. Notification sent via WebSocket to clients
 *   3. Database constraint violation occurs
 *   4. Transaction rolls back
 *   5. Result: Clients notified of change that never happened
 *
 * With @TransactionalEventListener(phase = AFTER_COMMIT):
 *   1. Service publishes event
 *   2. Event held until transaction completes
 *   3. If commit succeeds → notification sent
 *   4. If rollback occurs → notification never sent
 *   5. Result: Notifications always reflect actual database state
 */
@Component
public class EventNotificationListener {

    private final NotificationService notificationService;

    @Autowired
    public EventNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Listens for entity change events and sends notifications after transaction commit.
     *
     * The AFTER_COMMIT phase ensures this method only executes if the transaction
     * successfully commits. If the transaction rolls back, this listener will never
     * be invoked, preventing phantom notifications.
     *
     * The @Async annotation ensures notifications are sent on a separate thread pool,
     * outside the HTTP request transaction context. This is critical for JMS messaging
     * to ensure messages are committed immediately to the broker rather than being
     * held in the request thread's transaction scope.
     *
     * @param event The entity change event containing action, entity type, and entity ID
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEntityChangeEvent(EntityChangeEvent event) {
        notificationService.sendNotification(
                event.getAction(),
                event.getEntityType(),
                event.getEntityId()
        );
    }
}
