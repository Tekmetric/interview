package com.interview.util;

import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

/**
 * Utility class for working with correlation IDs.
 *
 * <p>Provides convenient methods to access the current request's correlation ID
 * from anywhere in the application code.
 */
@UtilityClass
public class CorrelationUtil {

    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    /**
     * Get the current correlation ID from MDC.
     *
     * @return correlation ID or null if not set
     */
    public static String getCurrentCorrelationId() {
        return MDC.get(CORRELATION_ID_MDC_KEY);
    }

    /**
     * Get the current correlation ID with fallback.
     *
     * @param fallback value to return if correlation ID is not set
     * @return correlation ID or fallback value
     */
    public static String getCurrentCorrelationId(String fallback) {
        String correlationId = MDC.get(CORRELATION_ID_MDC_KEY);
        return correlationId != null ? correlationId : fallback;
    }

    /**
     * Check if correlation ID is present.
     *
     * @return true if correlation ID is set
     */
    public static boolean hasCorrelationId() {
        return MDC.get(CORRELATION_ID_MDC_KEY) != null;
    }
}