package com.interview;

import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.dto.UpdateWidgetCommand;
import com.interview.command.handler.CreateWidgetHandler;
import com.interview.command.handler.DeleteWidgetHandler;
import com.interview.command.handler.UpdateWidgetHandler;
import com.interview.query.handler.GetAllWidgetsHandler;
import com.interview.query.handler.GetWidgetByIdHandler;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class MicrometerMetricsTest {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private CreateWidgetHandler createWidgetHandler;

    @Autowired
    private UpdateWidgetHandler updateWidgetHandler;

    @Autowired
    private DeleteWidgetHandler deleteWidgetHandler;

    @Autowired
    private GetAllWidgetsHandler getAllWidgetsHandler;

    @Autowired
    private GetWidgetByIdHandler getWidgetByIdHandler;

    @Test
    public void testMicrometerTimersAreRecorded() {
        // Create a widget
        CreateWidgetCommand createCommand = new CreateWidgetCommand("Metrics Test Widget", "Testing Micrometer metrics");
        var createdWidget = createWidgetHandler.handle(createCommand);
        assertNotNull(createdWidget);
        assertNotNull(createdWidget.getId());

        // Get all widgets
        getAllWidgetsHandler.handle();

        // Get widget by ID
        getWidgetByIdHandler.handle(createdWidget.getId());

        // Update the widget
        UpdateWidgetCommand updateCommand = new UpdateWidgetCommand("Updated Metrics Widget", "Updated description");
        updateWidgetHandler.handle(createdWidget.getId(), updateCommand);

        // Delete the widget
        deleteWidgetHandler.handle(createdWidget.getId());

        // Verify timers exist and have recorded metrics
        Timer createTimer = meterRegistry.find("widget.create").timer();
        assertNotNull(createTimer, "widget.create timer should exist");
        assertTrue(createTimer.count() > 0, "widget.create timer should have recorded at least one execution");
        System.out.println("widget.create - Count: " + createTimer.count() + ", Total Time: " + createTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) + "ms");

        Timer getAllTimer = meterRegistry.find("widget.getAll").timer();
        assertNotNull(getAllTimer, "widget.getAll timer should exist");
        assertTrue(getAllTimer.count() > 0, "widget.getAll timer should have recorded at least one execution");
        System.out.println("widget.getAll - Count: " + getAllTimer.count() + ", Total Time: " + getAllTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) + "ms");

        Timer getByIdTimer = meterRegistry.find("widget.getById").timer();
        assertNotNull(getByIdTimer, "widget.getById timer should exist");
        assertTrue(getByIdTimer.count() > 0, "widget.getById timer should have recorded at least one execution");
        System.out.println("widget.getById - Count: " + getByIdTimer.count() + ", Total Time: " + getByIdTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) + "ms");

        Timer updateTimer = meterRegistry.find("widget.update").timer();
        assertNotNull(updateTimer, "widget.update timer should exist");
        assertTrue(updateTimer.count() > 0, "widget.update timer should have recorded at least one execution");
        System.out.println("widget.update - Count: " + updateTimer.count() + ", Total Time: " + updateTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) + "ms");

        Timer deleteTimer = meterRegistry.find("widget.delete").timer();
        assertNotNull(deleteTimer, "widget.delete timer should exist");
        assertTrue(deleteTimer.count() > 0, "widget.delete timer should have recorded at least one execution");
        System.out.println("widget.delete - Count: " + deleteTimer.count() + ", Total Time: " + deleteTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) + "ms");

        // Print summary
        System.out.println("\n=== Micrometer Timers Summary ===");
        System.out.println("All handler methods are being timed by Micrometer!");
        System.out.println("Timers recorded: 5/5");
        System.out.println("Total operations: " +
            (createTimer.count() + getAllTimer.count() + getByIdTimer.count() +
             updateTimer.count() + deleteTimer.count()));
    }
}
