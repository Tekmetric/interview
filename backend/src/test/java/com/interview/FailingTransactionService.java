package com.interview;

import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.entity.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test service to demonstrate transaction rollback on exceptions.
 * This service is used in TransactionalityTest.
 */
@Service
public class FailingTransactionService {

    @Autowired
    private WidgetCommandRepository widgetCommandRepository;

    /**
     * Creates a widget but throws an exception before the transaction commits.
     * The @Transactional annotation ensures this runs in a transaction that will rollback on exception.
     */
    @Transactional("commandTransactionManager")
    public void createWidgetThenFail() {
        Widget widget = new Widget("Doomed Widget", "This will be rolled back");
        widgetCommandRepository.save(widget);

        System.out.println("Widget saved to database (but transaction not committed yet)...");

        // Simulate an error that causes transaction to rollback
        throw new RuntimeException("Simulated error - transaction will rollback!");
    }
}
