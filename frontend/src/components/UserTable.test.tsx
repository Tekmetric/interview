import { fireEvent, render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { SortConfig, TableData } from '../types';
import { UserTable } from './UserTable';

// Mock the useMediaQuery hook
vi.mock('../hooks/useMediaQuery', () => ({
  useIsDesktop: vi.fn(() => true), // Default to desktop view for tests
}));

// Mock data for testing
const mockData: TableData[] = [
  {
    id: '1',
    name: 'John Doe',
    email: 'john@example.com',
    phone: '+1234567890',
    company: 'Acme Corp',
    status: 'Active',
    createdAt: '2024-01-15T10:30:00Z',
  },
  {
    id: '2',
    name: 'Jane Smith',
    email: 'jane@example.com',
    phone: '+0987654321',
    company: 'Tech Inc',
    status: 'Inactive',
    createdAt: '2024-01-20T14:45:00Z',
  },
];

const defaultProps = {
  data: mockData,
  sortConfig: { key: 'name', direction: 'asc' } as SortConfig,
  onSort: vi.fn(),
  onEdit: vi.fn(),
  onDelete: vi.fn(),
  onView: vi.fn(),
};

describe('UserTable', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Data Display', () => {
    it('renders table with user data', () => {
      render(<UserTable {...defaultProps} />);

      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('jane@example.com')).toBeInTheDocument();
      expect(screen.getByText('Acme Corp')).toBeInTheDocument();
      expect(screen.getByText('Active')).toBeInTheDocument();
    });

    it('shows empty state when no data provided', () => {
      render(<UserTable {...defaultProps} data={[]} />);

      expect(screen.getByText(/no data found/i)).toBeInTheDocument();
    });

    it('displays status with correct text', () => {
      render(<UserTable {...defaultProps} />);

      expect(screen.getByText('Active')).toBeInTheDocument();
      expect(screen.getByText('Inactive')).toBeInTheDocument();
    });
  });

  describe('Sorting Functionality', () => {
    it('calls onSort when column header is clicked', () => {
      const onSort = vi.fn();

      render(<UserTable {...defaultProps} onSort={onSort} />);

      // Find a sortable header and click it
      const nameHeader = screen.getByText('Name').closest('th');
      if (nameHeader) {
        fireEvent.click(nameHeader);
        expect(onSort).toHaveBeenCalledWith('name');
      }
    });
  });

  describe('Action Buttons', () => {
    it('renders action buttons for each row', () => {
      render(<UserTable {...defaultProps} />);

      // Now only one view is rendered at a time
      expect(screen.getAllByText(/view/i)).toHaveLength(2);
      expect(screen.getAllByText(/edit/i)).toHaveLength(2);
      expect(screen.getAllByText(/delete/i)).toHaveLength(2);
    });

    it('calls onView when view button is clicked', () => {
      const onView = vi.fn();

      render(<UserTable {...defaultProps} onView={onView} />);

      const viewButtons = screen.getAllByText(/view/i);
      fireEvent.click(viewButtons[0]);

      expect(onView).toHaveBeenCalledWith(mockData[0]);
    });

    it('calls onEdit when edit button is clicked', () => {
      const onEdit = vi.fn();

      render(<UserTable {...defaultProps} onEdit={onEdit} />);

      const editButtons = screen.getAllByText(/edit/i);
      fireEvent.click(editButtons[0]);

      expect(onEdit).toHaveBeenCalledWith(mockData[0]);
    });
  });

  describe('Table Structure', () => {
    it('has proper table elements', () => {
      render(<UserTable {...defaultProps} />);

      expect(screen.getByRole('table')).toBeInTheDocument();
      expect(screen.getAllByRole('columnheader').length).toBeGreaterThan(0);
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1); // Header + data rows
    });

    it('displays column headers', () => {
      render(<UserTable {...defaultProps} />);

      expect(screen.getByText('Name')).toBeInTheDocument();
      expect(screen.getByText('Email')).toBeInTheDocument();
      expect(screen.getByText('Status')).toBeInTheDocument();
      // Company column is hidden on smaller screens but visible on xl+
      expect(screen.getByText('Company')).toBeInTheDocument();
    });
  });
});
