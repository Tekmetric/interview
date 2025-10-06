/**
 * Storybook Stories for GranularErrorBoundary Component
 */

import type { Meta, StoryObj } from '@storybook/react-webpack5';
import React from 'react';
import {
  GranularErrorBoundary,
  TableErrorBoundary,
  ChartErrorBoundary,
  SearchErrorBoundary,
} from './GranularErrorBoundary';

const meta: Meta<typeof GranularErrorBoundary> = {
  title: 'Components/GranularErrorBoundary',
  component: GranularErrorBoundary,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'Component-level error boundaries with different fallback strategies. Prevents isolated component failures from crashing the entire application.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof GranularErrorBoundary>;

/**
 * Component that throws an error for testing
 */
const ErrorComponent: React.FC<{ message?: string }> = ({ message = 'Test error' }) => {
  throw new Error(message);
};

/**
 * Working component for contrast
 */
const WorkingComponent: React.FC = () => (
  <div className="p-4 bg-green-50 border border-green-200 rounded">
    <p className="text-green-800">✓ This component works correctly</p>
  </div>
);

/**
 * Default error boundary with recovery
 */
export const DefaultBoundary: Story = {
  args: {
    componentName: 'TestComponent',
    severity: 'medium',
    allowRecovery: true,
    children: <ErrorComponent message="Something went wrong in this component" />,
  },
};

/**
 * High severity error
 */
export const HighSeverity: Story = {
  args: {
    componentName: 'CriticalComponent',
    severity: 'high',
    allowRecovery: true,
    children: <ErrorComponent message="Critical error occurred" />,
  },
  parameters: {
    docs: {
      description: {
        story: 'High severity errors are logged with higher priority.',
      },
    },
  },
};

/**
 * No recovery allowed
 */
export const NoRecovery: Story = {
  args: {
    componentName: 'UnrecoverableComponent',
    severity: 'critical',
    allowRecovery: false,
    children: <ErrorComponent message="Unrecoverable error" />,
  },
  parameters: {
    docs: {
      description: {
        story: 'Some errors cannot be recovered from - no retry button is shown.',
      },
    },
  },
};

/**
 * Custom fallback UI
 */
export const CustomFallback: Story = {
  args: {
    componentName: 'CustomFallbackComponent',
    severity: 'medium',
    allowRecovery: true,
    fallback: (error, reset) => (
      <div className="p-6 bg-purple-50 border-2 border-purple-300 rounded-lg">
        <h3 className="text-lg font-bold text-purple-800 mb-2">
          Custom Error Fallback
        </h3>
        <p className="text-purple-600 mb-4">{error.message}</p>
        <button
          onClick={reset}
          className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700"
        >
          Retry
        </button>
      </div>
    ),
    children: <ErrorComponent message="Custom error UI demo" />,
  },
  parameters: {
    docs: {
      description: {
        story: 'You can provide custom fallback UI for specific error scenarios.',
      },
    },
  },
};

/**
 * Working component (no error)
 */
export const NoError: Story = {
  args: {
    componentName: 'WorkingComponent',
    severity: 'low',
    allowRecovery: true,
    children: <WorkingComponent />,
  },
  parameters: {
    docs: {
      description: {
        story: 'When no error occurs, children render normally.',
      },
    },
  },
};

/**
 * Table-specific error boundary
 */
export const TableError: Story = {
  render: () => (
    <TableErrorBoundary>
      <ErrorComponent message="Table rendering failed" />
    </TableErrorBoundary>
  ),
  parameters: {
    docs: {
      description: {
        story: 'Specialized error boundary for table components with custom fallback.',
      },
    },
  },
};

/**
 * Chart-specific error boundary (silent failure)
 */
export const ChartError: Story = {
  render: () => (
    <div className="p-4 border border-gray-200 rounded">
      <div className="mb-2 text-sm font-medium text-gray-700">Pokemon Stats</div>
      <ChartErrorBoundary>
        <ErrorComponent message="Chart render error" />
      </ChartErrorBoundary>
    </div>
  ),
  parameters: {
    docs: {
      description: {
        story:
          'Chart errors fail silently with minimal UI impact - shows "Chart unavailable".',
      },
    },
  },
};

/**
 * Search-specific error boundary
 */
export const SearchError: Story = {
  render: () => (
    <div className="p-4">
      <label className="block text-sm font-medium text-gray-700 mb-2">Search Pokemon</label>
      <SearchErrorBoundary>
        <ErrorComponent message="Search component crashed" />
      </SearchErrorBoundary>
    </div>
  ),
  parameters: {
    docs: {
      description: {
        story: 'Low-severity errors for search functionality with inline recovery.',
      },
    },
  },
};

/**
 * Multiple boundaries (isolation demonstration)
 */
export const MultipleBoundaries: Story = {
  render: () => (
    <div className="space-y-4">
      <div>
        <h3 className="text-sm font-semibold mb-2">Component A (Error)</h3>
        <GranularErrorBoundary componentName="ComponentA" severity="medium">
          <ErrorComponent message="Component A failed" />
        </GranularErrorBoundary>
      </div>

      <div>
        <h3 className="text-sm font-semibold mb-2">Component B (Working)</h3>
        <GranularErrorBoundary componentName="ComponentB" severity="medium">
          <WorkingComponent />
        </GranularErrorBoundary>
      </div>

      <div>
        <h3 className="text-sm font-semibold mb-2">Component C (Error)</h3>
        <GranularErrorBoundary componentName="ComponentC" severity="high">
          <ErrorComponent message="Component C crashed" />
        </GranularErrorBoundary>
      </div>
    </div>
  ),
  parameters: {
    docs: {
      description: {
        story:
          'Multiple error boundaries isolate failures - Component B continues to work even when A and C fail.',
      },
    },
  },
};
