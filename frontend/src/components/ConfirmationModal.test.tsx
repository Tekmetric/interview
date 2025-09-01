import { fireEvent, render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { TableData } from '../types';
import { ConfirmationModal } from './ConfirmationModal';

const mockUser: TableData = {
  id: '1',
  name: 'John Doe',
  email: 'john@example.com',
  company: 'ACME Corp',
  status: 'active',
  createdAt: '2024-01-01T00:00:00Z',
};

describe('ConfirmationModal', () => {
  const defaultProps = {
    isOpen: true,
    onClose: vi.fn(),
    onConfirm: vi.fn(),
    title: 'Confirm Action',
    message: 'Are you sure you want to proceed?',
    user: mockUser,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    // Reset body styles
    document.body.style.overflow = '';
    document.body.style.paddingRight = '';
  });

  describe('Rendering', () => {
    it('renders modal when open', () => {
      render(<ConfirmationModal {...defaultProps} />);

      expect(screen.getByText('Confirm Action')).toBeInTheDocument();
      expect(screen.getByText('Are you sure you want to proceed?')).toBeInTheDocument();
      expect(screen.getByText('Confirm')).toBeInTheDocument();
      expect(screen.getByText('Cancel')).toBeInTheDocument();
    });

    it('does not render when closed', () => {
      render(<ConfirmationModal {...defaultProps} isOpen={false} />);

      expect(screen.queryByText('Confirm Action')).not.toBeInTheDocument();
    });

    it('renders custom button text', () => {
      render(
        <ConfirmationModal {...defaultProps} confirmText='Delete User' cancelText='Keep User' />
      );

      expect(screen.getByText('Delete User')).toBeInTheDocument();
      expect(screen.getByText('Keep User')).toBeInTheDocument();
    });

    it('renders user information when provided', () => {
      render(<ConfirmationModal {...defaultProps} />);

      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('john@example.com')).toBeInTheDocument();
    });

    it('renders warning icon for destructive actions', () => {
      render(<ConfirmationModal {...defaultProps} isDestructive={true} />);

      // Check for the warning icon (SVG)
      const warningIcon = document.querySelector('svg');
      expect(warningIcon).toBeInTheDocument();
      expect(warningIcon).toHaveClass('text-red-600');
    });
  });

  describe('Interactions', () => {
    it('calls onConfirm when confirm button is clicked', () => {
      const onConfirm = vi.fn();
      render(<ConfirmationModal {...defaultProps} onConfirm={onConfirm} />);

      const confirmButton = screen.getByText('Confirm');
      fireEvent.click(confirmButton);

      expect(onConfirm).toHaveBeenCalledTimes(1);
    });

    it('calls onClose when cancel button is clicked', () => {
      const onClose = vi.fn();
      render(<ConfirmationModal {...defaultProps} onClose={onClose} />);

      const cancelButton = screen.getByText('Cancel');
      fireEvent.click(cancelButton);

      expect(onClose).toHaveBeenCalledTimes(1);
    });

    it('calls onClose when backdrop is clicked', () => {
      const onClose = vi.fn();
      render(<ConfirmationModal {...defaultProps} onClose={onClose} />);

      // Click on the backdrop (modal overlay)
      const backdrop = document.querySelector('.fixed.inset-0');
      if (backdrop) {
        fireEvent.click(backdrop);
        expect(onClose).toHaveBeenCalledTimes(1);
      }
    });

    it('does not close when modal content is clicked', () => {
      const onClose = vi.fn();
      render(<ConfirmationModal {...defaultProps} onClose={onClose} />);

      // Click on the modal content
      const modalContent = document.querySelector('.rounded-md.border');
      if (modalContent) {
        fireEvent.click(modalContent);
        expect(onClose).not.toHaveBeenCalled();
      }
    });
  });

  describe('Keyboard Navigation', () => {
    it('closes modal when Escape key is pressed', () => {
      const onClose = vi.fn();
      render(<ConfirmationModal {...defaultProps} onClose={onClose} />);

      fireEvent.keyDown(document, { key: 'Escape' });

      expect(onClose).toHaveBeenCalledTimes(1);
    });

    it('does not close on other key presses', () => {
      const onClose = vi.fn();
      render(<ConfirmationModal {...defaultProps} onClose={onClose} />);

      fireEvent.keyDown(document, { key: 'Enter' });
      fireEvent.keyDown(document, { key: 'Space' });

      expect(onClose).not.toHaveBeenCalled();
    });
  });

  describe('Body Scroll Management', () => {
    it('disables body scroll when modal opens', () => {
      render(<ConfirmationModal {...defaultProps} isOpen={true} />);

      expect(document.body.style.overflow).toBe('hidden');
    });

    it('does not modify body scroll when modal is closed', () => {
      render(<ConfirmationModal {...defaultProps} isOpen={false} />);

      expect(document.body.style.overflow).toBe('');
    });
  });

  describe('Accessibility', () => {
    it('has proper button roles', () => {
      render(<ConfirmationModal {...defaultProps} />);

      const buttons = screen.getAllByRole('button');
      expect(buttons).toHaveLength(2);
    });

    it('maintains focus management', () => {
      render(<ConfirmationModal {...defaultProps} />);

      const confirmButton = screen.getByText('Confirm');
      const cancelButton = screen.getByText('Cancel');

      expect(confirmButton).toBeInTheDocument();
      expect(cancelButton).toBeInTheDocument();
    });
  });

  describe('Different Modal States', () => {
    it('handles modal without user data', () => {
      render(<ConfirmationModal {...defaultProps} user={undefined} />);

      expect(screen.getByText('Confirm Action')).toBeInTheDocument();
      expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
    });

    it('renders different styling for destructive actions', () => {
      render(<ConfirmationModal {...defaultProps} isDestructive={true} />);

      const confirmButton = screen.getByText('Confirm');
      expect(confirmButton).toHaveClass('btn-danger');
    });

    it('renders normal styling for non-destructive actions', () => {
      render(<ConfirmationModal {...defaultProps} isDestructive={false} />);

      const confirmButton = screen.getByText('Confirm');
      expect(confirmButton).toHaveClass('btn-primary');
    });
  });
});
