import React from 'react';
import ErrorBoundary from './ErrorBoundary';

const ThrowError = ({ shouldThrow, message }) => {
  if (shouldThrow) {
    throw new Error(message || 'Test error from story');
  }
  return <div>No error - component renders successfully</div>;
};

export default {
  title: 'Components/ErrorBoundary',
  component: ErrorBoundary,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const NoError = {
  render: () => (
    <ErrorBoundary>
      <div className="p-4 bg-green-100 rounded-lg">
        <p className="text-green-800">Content renders normally when there's no error</p>
      </div>
    </ErrorBoundary>
  ),
};

export const WithError = {
  render: () => (
    <ErrorBoundary>
      <ThrowError shouldThrow={true} message="Example error in component" />
    </ErrorBoundary>
  ),
};

export const CustomFallback = {
  render: () => (
    <ErrorBoundary
      fallback={(error, reset) => (
        <div className="p-6 bg-orange-100 rounded-lg text-center">
          <h3 className="text-xl font-bold text-orange-800 mb-2">Custom Error UI</h3>
          <p className="text-orange-600 mb-4">{error?.message}</p>
          <button
            onClick={reset}
            className="px-4 py-2 bg-orange-600 text-white rounded hover:bg-orange-700"
          >
            Try Again
          </button>
        </div>
      )}
    >
      <ThrowError shouldThrow={true} message="Custom fallback example" />
    </ErrorBoundary>
  ),
};
