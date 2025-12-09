package com.interview;

import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.dto.UpdateWidgetCommand;
import com.interview.command.handler.CreateWidgetHandler;
import com.interview.command.handler.UpdateWidgetHandler;
import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.entity.Widget;
import com.interview.query.repository.WidgetQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TransactionalityTest {

    @Autowired
    private WidgetCommandRepository widgetCommandRepository;

    @Autowired
    private WidgetQueryRepository widgetQueryRepository;

    @Autowired
    private CreateWidgetHandler createWidgetHandler;

    @Autowired
    private UpdateWidgetHandler updateWidgetHandler;

    @Autowired
    private FailingTransactionService failingTransactionService;

    @BeforeEach
    void setUp() {
        // Clean up databases before each test
        widgetCommandRepository.deleteAll();
        widgetQueryRepository.deleteAll();
    }

    /**
     * Test 1: Transaction Rollback on Exception
     *
     * This test demonstrates that when an exception occurs during a transaction,
     * all database changes within that transaction are rolled back.
     *
     * Scenario:
     * 1. Count initial widgets
     * 2. Attempt to create a widget in a transaction that throws an exception
     * 3. Verify that the widget was NOT persisted (transaction rolled back)
     */
    @Test
    public void testTransactionRollbackOnException() {
        System.out.println("\n=== Test 1: Transaction Rollback on Exception ===");

        // Count widgets before the failed transaction
        long countBefore = widgetCommandRepository.count();
        System.out.println("Widget count before failed transaction: " + countBefore);

        // Attempt to create a widget but throw an exception mid-transaction
        try {
            failingTransactionService.createWidgetThenFail();
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            System.out.println("Exception caught as expected: " + e.getMessage());
        }

        // Verify the transaction was rolled back - widget should NOT exist
        long countAfter = widgetCommandRepository.count();
        System.out.println("Widget count after failed transaction: " + countAfter);

        assertEquals(countBefore, countAfter,
            "Widget count should remain unchanged - transaction should have been rolled back");

        System.out.println("✓ Transaction rollback verified: No widget was persisted despite save() being called");
        System.out.println("✓ This proves that @Transactional annotation properly rolls back on exceptions\n");
    }

    /**
     * Test 2: Optimistic Locking
     *
     * This test demonstrates optimistic locking using JPA's @Version annotation.
     * Optimistic locking prevents lost updates when multiple transactions try to modify
     * the same entity concurrently.
     *
     * Scenario:
     * 1. Create a widget with version 0
     * 2. Load the same widget in two different "transactions" (simulated)
     * 3. Update and save from first transaction (version becomes 1)
     * 4. Try to update and save from second transaction using stale data (still version 0)
     * 5. Verify that OptimisticLockException is thrown for the second update
     */
    @Test
    public void testOptimisticLocking() {
        System.out.println("\n=== Test 2: Optimistic Locking ===");

        // Create initial widget
        CreateWidgetCommand createCommand = new CreateWidgetCommand(
            "Concurrency Test Widget",
            "Testing optimistic locking"
        );
        Widget createdWidget = createWidgetHandler.handle(createCommand);
        Long widgetId = createdWidget.getId();
        Long initialVersion = createdWidget.getVersion();

        System.out.println("Created widget with ID: " + widgetId + ", Version: " + initialVersion);

        // Simulate two concurrent transactions by loading the entity twice
        Widget widget1 = widgetCommandRepository.findById(widgetId).orElseThrow();
        Widget widget2 = widgetCommandRepository.findById(widgetId).orElseThrow();

        System.out.println("Loaded widget twice (simulating two concurrent transactions)");
        System.out.println("Widget 1 - Version: " + widget1.getVersion());
        System.out.println("Widget 2 - Version: " + widget2.getVersion());
        assertEquals(widget1.getVersion(), widget2.getVersion(), "Both should have same version initially");

        // First transaction: Update and save widget1
        widget1.setName("Updated by Transaction 1");
        Widget savedWidget1 = widgetCommandRepository.save(widget1);
        System.out.println("\nTransaction 1 completed - Version incremented to: " + savedWidget1.getVersion());

        // Second transaction: Try to update and save widget2 (which has stale version)
        widget2.setName("Updated by Transaction 2");
        System.out.println("Transaction 2 attempting to save with stale version: " + widget2.getVersion());

        // This should throw OptimisticLockException because widget2 has stale version
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            widgetCommandRepository.save(widget2);
            widgetCommandRepository.flush(); // Force the exception to occur
        }, "Expected OptimisticLockingFailureException when saving entity with stale version");

        System.out.println("✓ OptimisticLockingFailureException thrown as expected!");

        // Verify the first transaction's changes were persisted
        Widget finalWidget = widgetCommandRepository.findById(widgetId).orElseThrow();
        assertEquals("Updated by Transaction 1", finalWidget.getName(),
            "Only Transaction 1's changes should be persisted");
        assertEquals(savedWidget1.getVersion(), finalWidget.getVersion(),
            "Version should match Transaction 1's updated version");

        System.out.println("✓ Only Transaction 1's changes were persisted");
        System.out.println("✓ This proves @Version prevents lost updates in concurrent scenarios\n");
    }

    /**
     * Test 3: Version Incrementation
     *
     * This test verifies that the @Version field is automatically incremented
     * with each update operation.
     */
    @Test
    public void testVersionIncrementation() {
        System.out.println("\n=== Test 3: Version Incrementation ===");

        // Create widget
        CreateWidgetCommand createCommand = new CreateWidgetCommand(
            "Version Test Widget",
            "Testing version incrementation"
        );
        Widget widget = createWidgetHandler.handle(createCommand);
        Long widgetId = widget.getId();
        Long version0 = widget.getVersion();

        System.out.println("Created widget - Version: " + version0);
        assertNotNull(version0, "Version should be set after creation");

        // First update
        UpdateWidgetCommand updateCommand1 = new UpdateWidgetCommand(
            "Version Test Widget - Update 1",
            "First update"
        );
        Widget updated1 = updateWidgetHandler.handle(widgetId, updateCommand1).orElseThrow();
        Long version1 = updated1.getVersion();

        System.out.println("After first update - Version: " + version1);
        assertTrue(version1 > version0, "Version should increment after first update");

        // Second update
        UpdateWidgetCommand updateCommand2 = new UpdateWidgetCommand(
            "Version Test Widget - Update 2",
            "Second update"
        );
        Widget updated2 = updateWidgetHandler.handle(widgetId, updateCommand2).orElseThrow();
        Long version2 = updated2.getVersion();

        System.out.println("After second update - Version: " + version2);
        assertTrue(version2 > version1, "Version should increment after second update");

        System.out.println("✓ Version incremented with each update: " + version0 + " → " + version1 + " → " + version2);
        System.out.println("✓ This proves JPA is managing the @Version field automatically\n");
    }
}
