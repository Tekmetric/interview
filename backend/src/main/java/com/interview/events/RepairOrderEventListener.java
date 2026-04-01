package com.interview.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * This is a simple event listener that listens for RepairOrderCompletedEvent and logs the event details.
 * It's important to note that this is only triggered after the transaction is successfully committed.
 * We don't want to notify about a completed repair order if the transaction fails and the order isn't actually completed.
 * In a production system, this could be extended to invoke a notification service to send notifications, update other systems, etc.
 *
 * I can see a use-case for this in notifying customers when their repair orders are completed by email or SMS.
 */
@Component
public class RepairOrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(RepairOrderEventListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRepairOrderCompleted(RepairOrderCompletedEvent event) {
        log.info("RepairOrder COMPLETED: id={}, customer='{}', at={}", event.repairOrderId(), event.customerName(), event.completedAt());
        // TODO: Implement notification logic here (e.g., send email or SMS to customer)
    }
}
