import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';

import { Toast, ToastType } from './Toast';

describe('Toast', () => {
  const defaultProps = {
    id: 'test-toast',
    type: 'success' as ToastType,
    title: 'Test Toast',
    message: 'This is a test message',
    onClose: vi.fn(),
  };

  it('renders success toast with correct styling', () => {
    render(<Toast {...defaultProps} />);

    const toast = screen.getByRole('alert');
    expect(toast).toBeInTheDocument();
    expect(toast).toHaveClass('bg-green-50', 'border', 'border-green-200');
    expect(screen.getByText('Test Toast')).toBeInTheDocument();
    expect(screen.getByText('This is a test message')).toBeInTheDocument();
  });

  it('renders error toast with correct styling', () => {
    render(<Toast {...defaultProps} type='error' />);

    const toast = screen.getByRole('alert');
    expect(toast).toHaveClass('bg-red-50', 'border', 'border-red-200');
  });

  it('calls onClose when close button is clicked', () => {
    const onClose = vi.fn();
    render(<Toast {...defaultProps} onClose={onClose} />);

    const closeButton = screen.getByLabelText('Close notification');
    fireEvent.click(closeButton);

    expect(onClose).toHaveBeenCalledWith('test-toast');
  });

  it('renders without message', () => {
    render(<Toast {...defaultProps} message={undefined} />);

    expect(screen.getByText('Test Toast')).toBeInTheDocument();
    expect(screen.queryByText('This is a test message')).not.toBeInTheDocument();
  });

  it('has proper accessibility attributes', () => {
    render(<Toast {...defaultProps} />);

    const toast = screen.getByRole('alert');
    expect(toast).toHaveAttribute('aria-live', 'polite');
    expect(toast).toHaveAttribute('aria-atomic', 'true');

    const closeButton = screen.getByLabelText('Close notification');
    expect(closeButton).toHaveAttribute('aria-label', 'Close notification');
  });

  it('applies focus ring styling correctly for different types', () => {
    const { rerender } = render(<Toast {...defaultProps} type='success' />);
    let closeButton = screen.getByLabelText('Close notification');
    expect(closeButton).toHaveClass('focus:ring-green-500');

    rerender(<Toast {...defaultProps} type='error' />);
    closeButton = screen.getByLabelText('Close notification');
    expect(closeButton).toHaveClass('focus:ring-red-500');
  });
});
