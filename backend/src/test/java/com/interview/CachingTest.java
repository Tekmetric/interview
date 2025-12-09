package com.interview;

import com.interview.query.dto.WidgetDto;
import com.interview.query.handler.GetWidgetByIdHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CachingTest {

    @Autowired
    private GetWidgetByIdHandler getWidgetByIdHandler;

    @Test
    public void testCachingPerformance() {
        Long widgetId = 1L;

        // First request - should take at least 500ms due to Thread.sleep()
        long startTime1 = System.currentTimeMillis();
        Optional<WidgetDto> result1 = getWidgetByIdHandler.handle(widgetId);
        long duration1 = System.currentTimeMillis() - startTime1;

        assertTrue(result1.isPresent(), "Widget should exist");
        assertEquals("Widget A", result1.get().getName(), "Widget name should match");

        System.out.println("First request (cache MISS) took: " + duration1 + "ms");
        assertTrue(duration1 >= 500,
            "First request should take at least 500ms (actual: " + duration1 + "ms)");

        // Second request - should be fast (cached, no Thread.sleep())
        long startTime2 = System.currentTimeMillis();
        Optional<WidgetDto> result2 = getWidgetByIdHandler.handle(widgetId);
        long duration2 = System.currentTimeMillis() - startTime2;

        assertTrue(result2.isPresent(), "Widget should exist");
        assertEquals("Widget A", result2.get().getName(), "Widget name should match");

        System.out.println("Second request (cache HIT) took: " + duration2 + "ms");
        assertTrue(duration2 < 100,
            "Second request should be fast (< 100ms) due to caching (actual: " + duration2 + "ms)");

        // Third request - should also be fast (still cached)
        long startTime3 = System.currentTimeMillis();
        Optional<WidgetDto> result3 = getWidgetByIdHandler.handle(widgetId);
        long duration3 = System.currentTimeMillis() - startTime3;

        assertTrue(result3.isPresent(), "Widget should exist");
        assertEquals("Widget A", result3.get().getName(), "Widget name should match");

        System.out.println("Third request (cache HIT) took: " + duration3 + "ms");
        assertTrue(duration3 < 100,
            "Third request should be fast (< 100ms) due to caching (actual: " + duration3 + "ms)");

        // Verify performance improvement
        double improvement = (double) duration1 / duration2;
        System.out.println("\nPerformance improvement: " + String.format("%.1f", improvement) + "x faster with cache");
        assertTrue(improvement > 5,
            "Cache should provide at least 5x performance improvement (actual: " +
            String.format("%.1f", improvement) + "x)");
    }
}
