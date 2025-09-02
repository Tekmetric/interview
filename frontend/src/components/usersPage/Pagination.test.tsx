import { fireEvent, render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { PaginationInfo } from '../../types';
import { Pagination } from './Pagination';

describe('Pagination', () => {
  const defaultPagination: PaginationInfo = {
    currentPage: 1,
    totalPages: 5,
    pageSize: 10,
    totalRecords: 50,
  };

  const defaultProps = {
    pagination: defaultPagination,
    onPageChange: vi.fn(),
    onPageSizeChange: vi.fn(),
  };

  // Helper function to find text that may be split across multiple elements
  const getByTextContent = (text: string) => {
    return screen.getByText((_, element) => {
      return element?.textContent === text;
    });
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('renders pagination information', () => {
      render(<Pagination {...defaultProps} />);

      expect(getByTextContent('Showing 1 to 10 of 50 results')).toBeInTheDocument();
    });

    it('renders page size selector', () => {
      render(<Pagination {...defaultProps} />);

      const pageSizeSelect = screen.getByDisplayValue('10');
      expect(pageSizeSelect).toBeInTheDocument();
    });

    it('renders navigation buttons', () => {
      render(<Pagination {...defaultProps} />);

      expect(screen.getByText('Previous')).toBeInTheDocument();
      expect(screen.getByText('Next')).toBeInTheDocument();
    });

    it('renders current page number', () => {
      render(<Pagination {...defaultProps} />);

      // Look for the page button specifically, not just any "1" text
      expect(screen.getByRole('button', { name: 'Go to page 1' })).toBeInTheDocument();
      expect(screen.getByRole('button', { current: 'page' })).toBeInTheDocument();
    });
  });

  describe('Page Navigation', () => {
    it('calls onPageChange when next button is clicked', () => {
      const onPageChange = vi.fn();
      render(<Pagination {...defaultProps} onPageChange={onPageChange} />);

      const nextButton = screen.getByText('Next');
      fireEvent.click(nextButton);

      expect(onPageChange).toHaveBeenCalledWith(2);
    });

    it('calls onPageChange when previous button is clicked', () => {
      const onPageChange = vi.fn();
      const pagination = { ...defaultPagination, currentPage: 3 };
      render(<Pagination {...defaultProps} pagination={pagination} onPageChange={onPageChange} />);

      const prevButton = screen.getByText('Previous');
      fireEvent.click(prevButton);

      expect(onPageChange).toHaveBeenCalledWith(2);
    });

    it('disables previous button on first page', () => {
      render(<Pagination {...defaultProps} />);

      const prevButton = screen.getByText('Previous');
      expect(prevButton).toBeDisabled();
    });

    it('disables next button on last page', () => {
      const pagination = { ...defaultPagination, currentPage: 5 };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      const nextButton = screen.getByText('Next');
      expect(nextButton).toBeDisabled();
    });

    it('enables both buttons when on middle page', () => {
      const pagination = { ...defaultPagination, currentPage: 3 };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      const prevButton = screen.getByText('Previous');
      const nextButton = screen.getByText('Next');

      expect(prevButton).not.toBeDisabled();
      expect(nextButton).not.toBeDisabled();
    });
  });

  describe('Page Size Change', () => {
    it('calls onPageSizeChange when page size is changed', () => {
      const onPageSizeChange = vi.fn();
      render(<Pagination {...defaultProps} onPageSizeChange={onPageSizeChange} />);

      const pageSizeSelect = screen.getByDisplayValue('10');
      fireEvent.change(pageSizeSelect, { target: { value: '20' } });

      expect(onPageSizeChange).toHaveBeenCalledWith(20);
    });

    it('displays correct page size options', () => {
      render(<Pagination {...defaultProps} />);

      const pageSizeSelect = screen.getByDisplayValue('10');
      expect(pageSizeSelect).toBeInTheDocument();

      // Test that we can change to different page sizes
      fireEvent.change(pageSizeSelect, { target: { value: '5' } });
      fireEvent.change(pageSizeSelect, { target: { value: '20' } });
      fireEvent.change(pageSizeSelect, { target: { value: '50' } });
    });
  });

  describe('Pagination Info Display', () => {
    it('shows correct range for first page', () => {
      render(<Pagination {...defaultProps} />);

      expect(getByTextContent('Showing 1 to 10 of 50 results')).toBeInTheDocument();
    });

    it('shows correct range for middle page', () => {
      const pagination = { ...defaultPagination, currentPage: 3 };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      expect(getByTextContent('Showing 21 to 30 of 50 results')).toBeInTheDocument();
    });

    it('shows correct range for last page with partial data', () => {
      const pagination = {
        currentPage: 5,
        totalPages: 5,
        pageSize: 10,
        totalRecords: 47,
      };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      expect(getByTextContent('Showing 41 to 47 of 47 results')).toBeInTheDocument();
    });

    it('handles zero records', () => {
      const pagination = {
        currentPage: 1,
        totalPages: 0,
        pageSize: 10,
        totalRecords: 0,
      };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      // Component returns null for zero records
      expect(screen.queryByText(/showing/i)).not.toBeInTheDocument();
    });

    it('handles single record', () => {
      const pagination = {
        currentPage: 1,
        totalPages: 1,
        pageSize: 10,
        totalRecords: 1,
      };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      expect(getByTextContent('Showing 1 to 1 of 1 results')).toBeInTheDocument();
    });
  });

  describe('Edge Cases', () => {
    it('handles single page scenario', () => {
      const pagination = {
        currentPage: 1,
        totalPages: 1,
        pageSize: 10,
        totalRecords: 5,
      };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      const prevButton = screen.getByText('Previous');
      const nextButton = screen.getByText('Next');

      expect(prevButton).toBeDisabled();
      expect(nextButton).toBeDisabled();
    });

    it('handles no data scenario', () => {
      const pagination = {
        currentPage: 1,
        totalPages: 0,
        pageSize: 10,
        totalRecords: 0,
      };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      // Component returns null for no data
      expect(screen.queryByText('Previous')).not.toBeInTheDocument();
      expect(screen.queryByText('Next')).not.toBeInTheDocument();
    });

    it('handles large page numbers', () => {
      const pagination = {
        currentPage: 99,
        totalPages: 100,
        pageSize: 10,
        totalRecords: 1000,
      };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      expect(getByTextContent('Showing 981 to 990 of 1000 results')).toBeInTheDocument();
    });
  });

  describe('Accessibility', () => {
    it('has proper button labels for navigation', () => {
      render(<Pagination {...defaultProps} />);

      expect(screen.getByText('Previous')).toBeInTheDocument();
      expect(screen.getByText('Next')).toBeInTheDocument();
    });

    it('has proper select label for page size', () => {
      render(<Pagination {...defaultProps} />);

      const pageSizeSelect = screen.getByRole('combobox');
      expect(pageSizeSelect).toBeInTheDocument();
      expect(screen.getByText('Show:')).toBeInTheDocument();
    });

    it('maintains keyboard navigation', () => {
      // Use a middle page so both prev/next buttons are enabled
      const pagination = { ...defaultPagination, currentPage: 3 };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      const prevButton = screen.getByText('Previous');
      const nextButton = screen.getByText('Next');
      const pageSizeSelect = screen.getByRole('combobox');

      // Test that enabled buttons can receive focus
      nextButton.focus();
      expect(nextButton).toHaveFocus();

      prevButton.focus();
      expect(prevButton).toHaveFocus();

      pageSizeSelect.focus();
      expect(pageSizeSelect).toHaveFocus();
    });
  });

  describe('Different Page Sizes', () => {
    it('handles different page sizes correctly', () => {
      const pagination = {
        currentPage: 1,
        totalPages: 10,
        pageSize: 5,
        totalRecords: 50,
      };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      expect(getByTextContent('Showing 1 to 5 of 50 results')).toBeInTheDocument();
      expect(screen.getByDisplayValue('5')).toBeInTheDocument();
    });

    it('recalculates display range when page size changes', () => {
      const pagination = {
        currentPage: 2,
        totalPages: 3,
        pageSize: 20,
        totalRecords: 50,
      };
      render(<Pagination {...defaultProps} pagination={pagination} />);

      expect(getByTextContent('Showing 21 to 40 of 50 results')).toBeInTheDocument();
    });
  });
});
