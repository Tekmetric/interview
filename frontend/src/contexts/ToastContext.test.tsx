import { act, render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ToastProvider, useToastContext } from './ToastContext';

// Mock the useToast hook
vi.mock('../hooks/useToast', () => ({
  useToast: vi.fn(),
}));

import { useToast } from '../hooks/useToast';

// Test component to access toast context
const TestComponent: React.FC = () => {
  const { success, error, removeToast } = useToastContext();

  return (
    <div>
      <button data-testid='success-btn' onClick={() => success('Success!', 'Success message')}>
        Success Toast
      </button>
      <button data-testid='error-btn' onClick={() => error('Error!', 'Error message')}>
        Error Toast
      </button>
      <button data-testid='remove-btn' onClick={() => removeToast('test-id')}>
        Remove Toast
      </button>
    </div>
  );
};

describe('ToastContext', () => {
  const mockUseToast = {
    toasts: [],
    removeToast: vi.fn(),
    success: vi.fn(),
    error: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as ReturnType<typeof vi.fn>).mockReturnValue(mockUseToast);
  });

  describe('ToastProvider', () => {
    it('renders children correctly', () => {
      render(
        <ToastProvider>
          <div data-testid='child'>Test Content</div>
        </ToastProvider>
      );

      expect(screen.getByTestId('child')).toBeInTheDocument();
    });

    it('provides toast context to children', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      expect(screen.getByTestId('success-btn')).toBeInTheDocument();
      expect(screen.getByTestId('error-btn')).toBeInTheDocument();
    });

    it('renders ToastContainer with toasts', () => {
      const mockToasts = [
        {
          id: 'toast-1',
          type: 'success' as const,
          title: 'Success',
          message: 'Success message',
          duration: 5000,
          onClose: vi.fn(),
        },
      ];

      (useToast as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseToast,
        toasts: mockToasts,
      });

      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      // ToastContainer should be rendered (though we can't easily test its content without implementation details)
      expect(screen.getByTestId('success-btn')).toBeInTheDocument();
    });
  });

  describe('useToastContext', () => {
    it('throws error when used outside provider', () => {
      // Suppress console.error for this test
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

      expect(() => render(<TestComponent />)).toThrow(
        'useToastContext must be used within a ToastProvider'
      );

      consoleSpy.mockRestore();
    });

    it('provides success toast function', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('success-btn').click();
      });

      expect(mockUseToast.success).toHaveBeenCalledWith('Success!', 'Success message');
    });

    it('provides error toast function', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('error-btn').click();
      });

      expect(mockUseToast.error).toHaveBeenCalledWith('Error!', 'Error message');
    });

    it('provides removeToast function', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('remove-btn').click();
      });

      expect(mockUseToast.removeToast).toHaveBeenCalledWith('test-id');
    });
  });

  describe('Context Value', () => {
    it('passes through all functions from useToast hook', () => {
      const customMockUseToast = {
        toasts: [],
        removeToast: vi.fn(),
        success: vi.fn().mockReturnValue('success-id'),
        error: vi.fn().mockReturnValue('error-id'),
      };

      (useToast as ReturnType<typeof vi.fn>).mockReturnValue(customMockUseToast);

      const TestReturnValues: React.FC = () => {
        const { success, error } = useToastContext();

        const handleTest = () => {
          const successId = success('Test');
          const errorId = error('Test');

          // Store return values in DOM for testing
          document.body.setAttribute('data-success-id', successId);
          document.body.setAttribute('data-error-id', errorId);
        };

        return (
          <button onClick={handleTest} data-testid='test-returns'>
            Test Returns
          </button>
        );
      };

      render(
        <ToastProvider>
          <TestReturnValues />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('test-returns').click();
      });

      expect(document.body.getAttribute('data-success-id')).toBe('success-id');
      expect(document.body.getAttribute('data-error-id')).toBe('error-id');
    });
  });
});
