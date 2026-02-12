/**
 * Error Monitoring & Performance Tracking with Sentry
 *
 * Provides production error monitoring, performance metrics, and user feedback
 */

import * as Sentry from '@sentry/react';

/**
 * Environment configuration
 */
const IS_PRODUCTION = process.env.NODE_ENV === 'production';
const IS_DEVELOPMENT = process.env.NODE_ENV === 'development';

/**
 * Sentry DSN (should be in environment variables for production)
 * Replace with your actual Sentry DSN in production
 */
const SENTRY_DSN = process.env.REACT_APP_SENTRY_DSN || '';

/**
 * Initialize Sentry error monitoring
 *
 * Only runs in production to avoid noise during development
 */
export const initMonitoring = (): void => {
  // Only initialize in production or if explicitly enabled
  if (!IS_PRODUCTION && !process.env.REACT_APP_ENABLE_SENTRY) {
    console.log('[Monitoring] Sentry disabled in development');
    return;
  }

  if (!SENTRY_DSN) {
    console.warn('[Monitoring] Sentry DSN not configured');
    return;
  }

  Sentry.init({
    dsn: SENTRY_DSN,

    // Environment
    environment: process.env.NODE_ENV || 'development',

    // Release tracking
    release: process.env.REACT_APP_VERSION || 'unknown',

    // Performance Monitoring
    integrations: [
      Sentry.browserTracingIntegration(),
      Sentry.replayIntegration(),
    ],

    // Performance sample rate (0.0 to 1.0)
    // 1.0 = 100% of transactions sent to Sentry
    // Lower in production to reduce quota usage
    tracesSampleRate: IS_PRODUCTION ? 0.1 : 1.0,

    // Error sample rate
    sampleRate: IS_PRODUCTION ? 1.0 : 1.0,

    // Filter out errors
    beforeSend(event, hint) {
      const error = hint.originalException;

      // Filter out non-error exceptions
      if (!error) return null;

      // Filter out network errors (too noisy)
      if (error instanceof Error && error.message.includes('NetworkError')) {
        return null;
      }

      // Filter out canceled requests
      if (error instanceof Error && error.message.includes('canceled')) {
        return null;
      }

      // Add custom context
      event.contexts = {
        ...event.contexts,
        pokemon: {
          totalLoaded: localStorage.getItem('all_pokemon') ? 'cached' : 'not-cached',
        },
      };

      return event;
    },

    // Don't send errors in development
    enabled: IS_PRODUCTION || Boolean(process.env.REACT_APP_ENABLE_SENTRY),
  });

  console.log('[Monitoring] Sentry initialized');
};

/**
 * Capture custom error
 */
export const captureError = (error: Error, context?: Record<string, any>): void => {
  if (IS_DEVELOPMENT && !process.env.REACT_APP_ENABLE_SENTRY) {
    console.error('[Monitoring] Error captured:', error, context);
    return;
  }

  Sentry.captureException(error, {
    extra: context,
  });
};

/**
 * Capture custom message
 */
export const captureMessage = (
  message: string,
  level: Sentry.SeverityLevel = 'info',
  context?: Record<string, any>
): void => {
  if (IS_DEVELOPMENT && !process.env.REACT_APP_ENABLE_SENTRY) {
    console.log(`[Monitoring] ${level.toUpperCase()}: ${message}`, context);
    return;
  }

  Sentry.captureMessage(message, {
    level,
    extra: context,
  });
};

/**
 * Set user context for error tracking
 */
export const setUser = (user: { id: string; email?: string; username?: string }): void => {
  Sentry.setUser(user);
};

/**
 * Clear user context
 */
export const clearUser = (): void => {
  Sentry.setUser(null);
};

/**
 * Add breadcrumb for debugging
 */
export const addBreadcrumb = (
  message: string,
  category: string,
  data?: Record<string, any>
): void => {
  if (IS_DEVELOPMENT && !process.env.REACT_APP_ENABLE_SENTRY) {
    console.log(`[Breadcrumb] ${category}: ${message}`, data);
    return;
  }

  Sentry.addBreadcrumb({
    message,
    category,
    data,
    level: 'info',
  });
};

/**
 * Start performance transaction (Sentry v8 API)
 */
export const startTransaction = (
  name: string,
  op: string
): ReturnType<typeof Sentry.startSpan> | null => {
  if (IS_DEVELOPMENT && !process.env.REACT_APP_ENABLE_SENTRY) {
    return null;
  }

  return Sentry.startSpan({ name, op }, (span) => span);
};

/**
 * Custom error types for better monitoring
 */

export class ApiError extends Error {
  constructor(
    message: string,
    public statusCode?: number,
    public endpoint?: string
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export class CacheError extends Error {
  constructor(message: string, public operation?: string) {
    super(message);
    this.name = 'CacheError';
  }
}

export class ValidationError extends Error {
  constructor(message: string, public field?: string) {
    super(message);
    this.name = 'ValidationError';
  }
}

/**
 * Performance monitoring helpers
 */

export const measurePerformance = async <T>(
  name: string,
  operation: () => Promise<T>
): Promise<T> => {
  if (IS_DEVELOPMENT && !process.env.REACT_APP_ENABLE_SENTRY) {
    return await operation();
  }

  return await Sentry.startSpan(
    {
      name,
      op: 'custom',
    },
    async () => {
      try {
        return await operation();
      } catch (error) {
        Sentry.captureException(error);
        throw error;
      }
    }
  );
};

/**
 * React Error Boundary integration
 */
export const SentryErrorBoundary = Sentry.ErrorBoundary;

/**
 * Usage examples:
 *
 * ```typescript
 * // Initialize in index.tsx
 * initMonitoring();
 *
 * // Capture errors
 * try {
 *   await fetchData();
 * } catch (error) {
 *   captureError(error, { context: 'data-fetch' });
 * }
 *
 * // Add breadcrumbs
 * addBreadcrumb('User searched for Pikachu', 'user-action', { query: 'pikachu' });
 *
 * // Performance monitoring
 * const data = await measurePerformance('fetch-pokemon', () => fetchPokemon());
 * ```
 */
