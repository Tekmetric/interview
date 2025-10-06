import React from 'react';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import ErrorBoundary from './ErrorBoundary';

// Component that throws an error
const ThrowError = ({ shouldThrow, message }) => {
  if (shouldThrow) {
    throw new Error(message || 'Test error');
  }
  return <div>No error</div>;
};

// Suppress console.error for these tests since we expect errors
beforeAll(() => {
  jest.spyOn(console, 'error').mockImplementation(() => {});
});

afterAll(() => {
  console.error.mockRestore();
});

describe('ErrorBoundary', () => {
  it('renders children when there is no error', () => {
    const { getByText } = render(
      <ErrorBoundary>
        <div>Test content</div>
      </ErrorBoundary>
    );
    expect(getByText('Test content')).toBeInTheDocument();
  });

  it('renders error UI when a child component throws', () => {
    const { getByText } = render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} message="Something went wrong" />
      </ErrorBoundary>
    );

    expect(getByText('Oops! Something went wrong')).toBeInTheDocument();
    expect(getByText('Something went wrong')).toBeInTheDocument();
    expect(getByText('Try Again')).toBeInTheDocument();
  });

  it('renders custom fallback UI when provided', () => {
    const customFallback = (error) => (
      <div>Custom error: {error?.message || 'Unknown error'}</div>
    );

    const { getByText } = render(
      <ErrorBoundary fallback={customFallback}>
        <ThrowError shouldThrow={true} message="Custom error message" />
      </ErrorBoundary>
    );

    expect(getByText(/Custom error:/)).toBeInTheDocument();
  });

  it('calls reset handler when Try Again is clicked', () => {
    const { getByText } = render(
      <ErrorBoundary
        fallback={(error, reset) => (
          <div>
            <div>Error occurred</div>
            <button onClick={reset}>Reset</button>
          </div>
        )}
      >
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(getByText('Error occurred')).toBeInTheDocument();

    const resetButton = getByText('Reset');

    // Mock the setState to verify it's called
    const originalSetState = ErrorBoundary.prototype.setState;
    ErrorBoundary.prototype.setState = jest.fn(originalSetState);

    resetButton.click();

    // Verify setState was called (which happens in handleReset)
    expect(ErrorBoundary.prototype.setState).toHaveBeenCalled();

    // Restore original setState
    ErrorBoundary.prototype.setState = originalSetState;
  });

  it('calls onReset callback when provided', () => {
    const onResetMock = jest.fn();
    const { getByText } = render(
      <ErrorBoundary onReset={onResetMock}>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    const tryAgainButton = getByText('Try Again');
    tryAgainButton.click();

    expect(onResetMock).toHaveBeenCalledTimes(1);
  });

  it('shows error details in development mode', () => {
    const originalEnv = process.env.NODE_ENV;
    process.env.NODE_ENV = 'development';

    const { getByText } = render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} message="Dev error" />
      </ErrorBoundary>
    );

    expect(getByText('Error Details (Development Only)')).toBeInTheDocument();

    process.env.NODE_ENV = originalEnv;
  });
});
