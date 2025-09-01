import { render, screen } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { ToastType } from './Toast';
import ToastContainer, { ToastContainerProps } from './ToastContainer';

// Mock the Toast component to simplify testing
vi.mock('./Toast', () => ({
  Toast: ({ id, title, type, onClose }: any) => (
    <div
      data-testid={`toast-${id}`}
      role="alert"
      onClick={() => onClose(id)}
    >
      <div data-testid="toast-title">{title}</div>
      <div data-testid="toast-type">{type}</div>
    </div>
  ),
}));

describe('ToastContainer', () => {
  const createMockToast = (id: string, type: ToastType = 'success', title: string = 'Test Toast') => ({
    id,
    type,
    title,
    message: 'Test message',
    onClose: vi.fn(),
  });

  const defaultProps: ToastContainerProps = {
    toasts: [],
    onRemove: vi.fn(),
  };

  beforeEach(() => {
    // Clear the document body before each test
    document.body.innerHTML = '';
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('renders nothing when toasts array is empty', () => {
    const { container } = render(<ToastContainer {...defaultProps} />);
    expect(container.firstChild).toBeNull();
  });

  it('renders a single toast', () => {
    const toasts = [createMockToast('toast-1', 'success', 'Success Toast')];
    const props = { ...defaultProps, toasts };

    render(<ToastContainer {...props} />);

    expect(screen.getByTestId('toast-toast-1')).toBeInTheDocument();
    expect(screen.getByTestId('toast-title')).toHaveTextContent('Success Toast');
    expect(screen.getByTestId('toast-type')).toHaveTextContent('success');
  });

  it('renders multiple toasts', () => {
    const toasts = [
      createMockToast('toast-1', 'success', 'Success Toast'),
      createMockToast('toast-2', 'error', 'Error Toast'),
      createMockToast('toast-3', 'warning', 'Warning Toast'),
    ];
    const props = { ...defaultProps, toasts };

    render(<ToastContainer {...props} />);

    expect(screen.getByTestId('toast-toast-1')).toBeInTheDocument();
    expect(screen.getByTestId('toast-toast-2')).toBeInTheDocument();
    expect(screen.getByTestId('toast-toast-3')).toBeInTheDocument();
  });

  it('renders toasts in correct order', () => {
    const toasts = [
      createMockToast('toast-1', 'success', 'First Toast'),
      createMockToast('toast-2', 'error', 'Second Toast'),
    ];
    const props = { ...defaultProps, toasts };

    render(<ToastContainer {...props} />);

    const toastTitles = screen.getAllByTestId('toast-title');
    expect(toastTitles[0]).toHaveTextContent('First Toast');
    expect(toastTitles[1]).toHaveTextContent('Second Toast');
  });

  it('passes onRemove function to Toast components', () => {
    const onRemove = vi.fn();
    const toasts = [createMockToast('toast-1')];
    const props = { ...defaultProps, toasts, onRemove };

    render(<ToastContainer {...props} />);

    const toast = screen.getByTestId('toast-toast-1');
    toast.click();

    expect(onRemove).toHaveBeenCalledWith('toast-1');
  });

  it('renders container with correct accessibility attributes', () => {
    const toasts = [createMockToast('toast-1')];
    const props = { ...defaultProps, toasts };

    render(<ToastContainer {...props} />);

    // Since we're using createPortal, we need to check in document.body
    const container = document.body.querySelector('[aria-live="assertive"]');
    expect(container).toBeInTheDocument();
    expect(container).toHaveAttribute('aria-label', 'Notifications');
  });

  it('renders container with correct CSS classes', () => {
    const toasts = [createMockToast('toast-1')];
    const props = { ...defaultProps, toasts };

    render(<ToastContainer {...props} />);

    const container = document.body.querySelector('[aria-live="assertive"]');
    expect(container).toHaveClass(
      'pointer-events-none',
      'fixed',
      'inset-0',
      'z-50',
      'flex',
      'items-end',
      'justify-end',
      'p-6',
      'sm:items-start',
      'sm:justify-end'
    );
  });

  it('renders toast wrapper with animation styles', () => {
    const toasts = [createMockToast('toast-1')];
    const props = { ...defaultProps, toasts };

    render(<ToastContainer {...props} />);

    const toastWrapper = document.body.querySelector('[style*="animation"]');
    expect(toastWrapper).toBeInTheDocument();
    expect(toastWrapper).toHaveStyle('animation: slideInRight 0.3s ease-out');
  });

  it('renders toasts in a portal attached to document.body', () => {
    const toasts = [createMockToast('toast-1')];
    const props = { ...defaultProps, toasts };

    const { container } = render(<ToastContainer {...props} />);

    // The component itself should be empty (since it uses portal)
    expect(container.firstChild).toBeNull();

    // But the toast should be in document.body
    expect(document.body.querySelector('[data-testid="toast-toast-1"]')).toBeInTheDocument();
  });

  it('handles empty toasts array gracefully', () => {
    const props = { ...defaultProps, toasts: [] };
    const { container } = render(<ToastContainer {...props} />);

    expect(container.firstChild).toBeNull();
    expect(document.body.querySelector('[data-testid^="toast-"]')).not.toBeInTheDocument();
  });

  it('updates when toasts prop changes', () => {
    const initialToasts = [createMockToast('toast-1')];
    const { rerender } = render(<ToastContainer {...defaultProps} toasts={initialToasts} />);

    expect(screen.getByTestId('toast-toast-1')).toBeInTheDocument();

    const newToasts = [
      createMockToast('toast-1'),
      createMockToast('toast-2'),
    ];
    rerender(<ToastContainer {...defaultProps} toasts={newToasts} />);

    expect(screen.getByTestId('toast-toast-1')).toBeInTheDocument();
    expect(screen.getByTestId('toast-toast-2')).toBeInTheDocument();
  });

  it('removes toasts when they are no longer in the array', () => {
    const initialToasts = [
      createMockToast('toast-1'),
      createMockToast('toast-2'),
    ];
    const { rerender } = render(<ToastContainer {...defaultProps} toasts={initialToasts} />);

    expect(screen.getByTestId('toast-toast-1')).toBeInTheDocument();
    expect(screen.getByTestId('toast-toast-2')).toBeInTheDocument();

    const newToasts = [createMockToast('toast-1')];
    rerender(<ToastContainer {...defaultProps} toasts={newToasts} />);

    expect(screen.getByTestId('toast-toast-1')).toBeInTheDocument();
    expect(screen.queryByTestId('toast-toast-2')).not.toBeInTheDocument();
  });
});
