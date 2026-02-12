/**
 * Granular Error Boundary System
 *
 * Provides component-level error handling with different strategies:
 * - Isolated component failures (don't crash the whole app)
 * - Error recovery mechanisms
 * - Error reporting to monitoring services
 * - Fallback UI customization
 */

import React, { Component, ReactNode, ErrorInfo } from 'react';
import { logger } from '../../../lib/logger';

/**
 * Error severity levels for prioritization
 */
export type ErrorSeverity = 'low' | 'medium' | 'high' | 'critical';

/**
 * Error boundary configuration
 */
export interface ErrorBoundaryConfig {
  /** Component name for error reporting */
  componentName: string;

  /** Error severity level */
  severity?: ErrorSeverity;

  /** Whether to allow error recovery/retry */
  allowRecovery?: boolean;

  /** Custom error handler (e.g., send to Sentry) */
  onError?: (error: Error, errorInfo: ErrorInfo) => void;

  /** Fallback UI when error occurs */
  fallback?: (error: Error, reset: () => void) => ReactNode;

  /** Whether to log errors to console */
  logErrors?: boolean;
}

interface ErrorBoundaryProps extends ErrorBoundaryConfig {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
  errorInfo: ErrorInfo | null;
  errorCount: number;
}

/**
 * Granular Error Boundary Component
 *
 * Catches errors at the component level and provides recovery mechanisms
 *
 * @example
 * ```tsx
 * <GranularErrorBoundary
 *   componentName="PokemonTable"
 *   severity="high"
 *   allowRecovery={true}
 *   onError={(error) => Sentry.captureException(error)}
 * >
 *   <PokemonTable data={pokemon} />
 * </GranularErrorBoundary>
 * ```
 */
export class GranularErrorBoundary extends Component<
  ErrorBoundaryProps,
  ErrorBoundaryState
> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
      errorCount: 0,
    };
  }

  static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    return {
      hasError: true,
      error,
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    const { componentName, severity = 'medium', onError, logErrors = true } = this.props;

    // Increment error count
    this.setState((prevState) => ({
      errorInfo,
      errorCount: prevState.errorCount + 1,
    }));

    // Log error
    if (logErrors) {
      logger.error(`[${componentName}] Error caught:`, {
        error: error.message,
        severity,
        stack: error.stack,
        componentStack: errorInfo.componentStack,
        errorCount: this.state.errorCount + 1,
      });
    }

    // Call custom error handler (e.g., Sentry)
    if (onError) {
      try {
        onError(error, errorInfo);
      } catch (handlerError) {
        logger.error('Error in custom error handler:', handlerError);
      }
    }

    // Prevent infinite error loops
    if (this.state.errorCount >= 3) {
      logger.error(`[${componentName}] Too many errors (${this.state.errorCount}). Stopping recovery.`);
    }
  }

  handleReset = (): void => {
    const { componentName } = this.props;
    logger.info(`[${componentName}] Resetting error boundary`);

    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
      // Don't reset errorCount to prevent infinite loops
    });
  };

  render(): ReactNode {
    const { hasError, error, errorCount } = this.state;
    const { children, componentName, allowRecovery = true, fallback } = this.props;

    if (hasError && error) {
      // Prevent infinite error loops
      if (errorCount >= 3) {
        return (
          <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
            <div className="flex items-start gap-3">
              <span className="text-2xl" role="img" aria-label="Critical Error">
                🚨
              </span>
              <div>
                <h3 className="font-bold text-red-800">Critical Error</h3>
                <p className="text-sm text-red-600 mt-1">
                  {componentName} has encountered multiple errors and has been disabled.
                </p>
                <p className="text-xs text-red-500 mt-2">
                  Please refresh the page or contact support.
                </p>
              </div>
            </div>
          </div>
        );
      }

      // Use custom fallback if provided
      if (fallback) {
        return fallback(error, this.handleReset);
      }

      // Default fallback UI
      return (
        <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
          <div className="flex items-start gap-3">
            <span className="text-2xl" role="img" aria-label="Error">
              ⚠️
            </span>
            <div className="flex-1">
              <h3 className="font-bold text-yellow-800">Component Error</h3>
              <p className="text-sm text-yellow-700 mt-1">
                {componentName} encountered an error.
              </p>
              <details className="mt-2">
                <summary className="text-xs text-yellow-600 cursor-pointer hover:text-yellow-800">
                  Error details
                </summary>
                <pre className="text-xs text-yellow-600 mt-2 p-2 bg-yellow-100 rounded overflow-auto max-h-32">
                  {error.message}
                </pre>
              </details>
              {allowRecovery && (
                <button
                  onClick={this.handleReset}
                  className="mt-3 px-4 py-2 bg-yellow-600 text-white text-sm rounded hover:bg-yellow-700 transition-colors"
                >
                  Try Again
                </button>
              )}
            </div>
          </div>
        </div>
      );
    }

    return children;
  }
}

/**
 * Hook-based Error Boundary wrapper
 *
 * Allows using error boundaries with functional components
 */
export const withErrorBoundary = <P extends object>(
  Component: React.ComponentType<P>,
  config: ErrorBoundaryConfig
) => {
  return (props: P) => (
    <GranularErrorBoundary {...config}>
      <Component {...props} />
    </GranularErrorBoundary>
  );
};

/**
 * Specialized Error Boundaries for different use cases
 */

/**
 * Table Error Boundary
 * High severity, allows recovery
 */
export const TableErrorBoundary: React.FC<{ children: ReactNode }> = ({ children }) => (
  <GranularErrorBoundary
    componentName="PokemonTable"
    severity="high"
    allowRecovery={true}
    fallback={(error, reset) => (
      <div className="flex-1 flex flex-col items-center justify-center p-8 bg-red-50">
        <span className="text-4xl mb-4" role="img" aria-label="Error">
          😵
        </span>
        <h2 className="text-xl font-bold text-gray-800 mb-2">
          Table rendering error
        </h2>
        <p className="text-gray-600 mb-4 text-center max-w-md">
          {error.message || 'An unexpected error occurred while rendering the table'}
        </p>
        <button
          onClick={reset}
          className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Reload Table
        </button>
      </div>
    )}
  >
    {children}
  </GranularErrorBoundary>
);

/**
 * Chart Error Boundary
 * Medium severity, silent failure with minimal UI
 */
export const ChartErrorBoundary: React.FC<{ children: ReactNode }> = ({ children }) => (
  <GranularErrorBoundary
    componentName="Chart"
    severity="medium"
    allowRecovery={false}
    fallback={() => (
      <div className="text-xs text-gray-500 italic">Chart unavailable</div>
    )}
  >
    {children}
  </GranularErrorBoundary>
);

/**
 * Search Error Boundary
 * Low severity, allows recovery
 */
export const SearchErrorBoundary: React.FC<{ children: ReactNode }> = ({ children }) => (
  <GranularErrorBoundary
    componentName="Search"
    severity="low"
    allowRecovery={true}
    fallback={(error, reset) => (
      <div className="p-2 bg-yellow-50 border border-yellow-200 rounded text-xs">
        <span>Search error. </span>
        <button onClick={reset} className="text-blue-600 hover:underline">
          Reset
        </button>
      </div>
    )}
  >
    {children}
  </GranularErrorBoundary>
);

export default GranularErrorBoundary;
