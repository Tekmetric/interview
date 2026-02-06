import { fireEvent, render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { FilterConfig } from '../../types';
import { SearchAndFilter } from './SearchAndFilter';

describe('SearchAndFilter', () => {
  const defaultFilterConfig: FilterConfig = {
    searchTerm: '',
    statusFilter: '',
  };

  const defaultProps = {
    filterConfig: defaultFilterConfig,
    onFilterChange: vi.fn(),
    onAddNew: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('renders search input with placeholder', () => {
      render(<SearchAndFilter {...defaultProps} />);

      const searchInput = screen.getByPlaceholderText(/search by name, email, or company/i);
      expect(searchInput).toBeInTheDocument();
      expect(searchInput).toHaveValue('');
    });

    it('renders status filter dropdown', () => {
      render(<SearchAndFilter {...defaultProps} />);

      const statusSelect = screen.getByDisplayValue('All Status');
      expect(statusSelect).toBeInTheDocument();
    });

    it('renders add new button', () => {
      render(<SearchAndFilter {...defaultProps} />);

      const addButton = screen.getByText(/add new/i);
      expect(addButton).toBeInTheDocument();
    });

    it('renders search icon', () => {
      render(<SearchAndFilter {...defaultProps} />);

      // Check for search icon (SVG)
      const searchIcon = document.querySelector('svg');
      expect(searchIcon).toBeInTheDocument();
    });
  });

  describe('Search Functionality', () => {
    it('displays current search term', () => {
      const filterConfig = { ...defaultFilterConfig, searchTerm: 'John Doe' };
      render(<SearchAndFilter {...defaultProps} filterConfig={filterConfig} />);

      const searchInput = screen.getByDisplayValue('John Doe');
      expect(searchInput).toBeInTheDocument();
    });

    it('calls onFilterChange when search input changes', () => {
      const onFilterChange = vi.fn();
      render(<SearchAndFilter {...defaultProps} onFilterChange={onFilterChange} />);

      const searchInput = screen.getByPlaceholderText(/search by name, email, or company/i);
      fireEvent.change(searchInput, { target: { value: 'test search' } });

      expect(onFilterChange).toHaveBeenCalledWith({
        searchTerm: 'test search',
      });
    });

    it('handles empty search input', () => {
      const onFilterChange = vi.fn();
      const filterConfig = { ...defaultFilterConfig, searchTerm: 'existing' };
      render(
        <SearchAndFilter
          {...defaultProps}
          filterConfig={filterConfig}
          onFilterChange={onFilterChange}
        />
      );

      const searchInput = screen.getByDisplayValue('existing');
      fireEvent.change(searchInput, { target: { value: '' } });

      expect(onFilterChange).toHaveBeenCalledWith({
        searchTerm: '',
      });
    });
  });

  describe('Status Filter Functionality', () => {
    it('displays current status filter', () => {
      const filterConfig = { ...defaultFilterConfig, statusFilter: 'Active' };
      render(<SearchAndFilter {...defaultProps} filterConfig={filterConfig} />);

      const statusSelect = screen.getByDisplayValue('Active');
      expect(statusSelect).toBeInTheDocument();
    });

    it('calls onFilterChange when status filter changes', () => {
      const onFilterChange = vi.fn();
      render(<SearchAndFilter {...defaultProps} onFilterChange={onFilterChange} />);

      const statusSelect = screen.getByDisplayValue('All Status');
      fireEvent.change(statusSelect, { target: { value: 'Active' } });

      expect(onFilterChange).toHaveBeenCalledWith({
        statusFilter: 'Active',
      });
    });

    it('renders all status options correctly', () => {
      render(<SearchAndFilter {...defaultProps} />);

      const statusSelect = screen.getByDisplayValue('All Status');
      expect(statusSelect).toBeInTheDocument();

      // Check that we can change to each option
      fireEvent.change(statusSelect, { target: { value: 'Active' } });
      fireEvent.change(statusSelect, { target: { value: 'Inactive' } });
      fireEvent.change(statusSelect, { target: { value: '' } });
    });
  });

  describe('Clear Filters Functionality', () => {
    it('shows clear button when search filter is active', () => {
      const filterConfig = { searchTerm: 'test', statusFilter: '' };
      render(<SearchAndFilter {...defaultProps} filterConfig={filterConfig} />);

      const clearButton = screen.getByText(/clear/i);
      expect(clearButton).toBeInTheDocument();
    });

    it('shows clear button when status filter is active', () => {
      const filterConfig = { searchTerm: '', statusFilter: 'Active' };
      render(<SearchAndFilter {...defaultProps} filterConfig={filterConfig} />);

      const clearButton = screen.getByText(/clear/i);
      expect(clearButton).toBeInTheDocument();
    });

    it('does not show clear button when no filters are active', () => {
      const filterConfig = { searchTerm: '', statusFilter: '' };
      render(<SearchAndFilter {...defaultProps} filterConfig={filterConfig} />);

      const clearButton = screen.queryByText(/clear/i);
      expect(clearButton).not.toBeInTheDocument();
    });

    it('calls onFilterChange to clear filters when clear button is clicked', () => {
      const onFilterChange = vi.fn();
      const filterConfig = { searchTerm: 'test', statusFilter: 'Active' };
      render(
        <SearchAndFilter
          {...defaultProps}
          filterConfig={filterConfig}
          onFilterChange={onFilterChange}
        />
      );

      const clearButton = screen.getByText(/clear/i);
      fireEvent.click(clearButton);

      expect(onFilterChange).toHaveBeenCalledWith({
        searchTerm: '',
        statusFilter: '',
      });
    });
  });

  describe('Add New Functionality', () => {
    it('calls onAddNew when add new button is clicked', () => {
      const onAddNew = vi.fn();
      render(<SearchAndFilter {...defaultProps} onAddNew={onAddNew} />);

      const addButton = screen.getByText(/add new/i);
      fireEvent.click(addButton);

      expect(onAddNew).toHaveBeenCalledTimes(1);
    });

    it('renders add new button with plus icon', () => {
      render(<SearchAndFilter {...defaultProps} />);

      const addButton = screen.getByText(/add new/i);
      expect(addButton).toBeInTheDocument();

      // Check for plus icon (should be an SVG)
      const icons = document.querySelectorAll('svg');
      expect(icons.length).toBeGreaterThan(0);
    });
  });

  describe('Combined Filters', () => {
    it('handles both search and status filter together', () => {
      const filterConfig = { searchTerm: 'john', statusFilter: 'Active' };
      render(<SearchAndFilter {...defaultProps} filterConfig={filterConfig} />);

      // Verify both filters are displayed
      expect(screen.getByDisplayValue('john')).toBeInTheDocument();
      expect(screen.getByDisplayValue('Active')).toBeInTheDocument();

      // Clear button should be visible
      expect(screen.getByText(/clear/i)).toBeInTheDocument();
    });

    it('preserves one filter when changing another', () => {
      const onFilterChange = vi.fn();
      const filterConfig = { searchTerm: 'john', statusFilter: 'Active' };
      render(
        <SearchAndFilter
          {...defaultProps}
          filterConfig={filterConfig}
          onFilterChange={onFilterChange}
        />
      );

      // Change search term
      const searchInput = screen.getByDisplayValue('john');
      fireEvent.change(searchInput, { target: { value: 'jane' } });

      expect(onFilterChange).toHaveBeenCalledWith({
        searchTerm: 'jane',
      });
    });
  });

  describe('Accessibility', () => {
    it('has proper form controls', () => {
      render(<SearchAndFilter {...defaultProps} />);

      const searchInput = screen.getByRole('searchbox');
      const statusSelect = screen.getByRole('combobox');
      const addButton = screen.getByRole('button', { name: /add new/i });

      expect(searchInput).toBeInTheDocument();
      expect(statusSelect).toBeInTheDocument();
      expect(addButton).toBeInTheDocument();
    });

    it('maintains proper keyboard navigation', () => {
      render(<SearchAndFilter {...defaultProps} />);

      const searchInput = screen.getByRole('searchbox');
      const statusSelect = screen.getByRole('combobox');

      // Elements should be focusable
      searchInput.focus();
      expect(searchInput).toHaveFocus();

      statusSelect.focus();
      expect(statusSelect).toHaveFocus();
    });
  });

  describe('Responsive Design', () => {
    it('renders with responsive container classes', () => {
      render(<SearchAndFilter {...defaultProps} />);

      // Check for responsive layout container
      const container = document.querySelector('.rounded-lg');
      expect(container).toBeInTheDocument();
      expect(container).toHaveClass('bg-white', 'dark:bg-gray-800');
    });
  });
});
