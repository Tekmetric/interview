import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ToastProvider } from '../contexts/ToastContext';
import { UsersPage } from './UsersPage';

// Create mock functions and objects
const mockNavigate = vi.fn();
const mockSetSearchParams = vi.fn();
const mockSearchParams = new URLSearchParams();

// Mock React Router hooks
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useSearchParams: () => [mockSearchParams, mockSetSearchParams],
  };
});

// Mock the hooks
vi.mock('../hooks/useUserQueries', () => ({
  useUsers: vi.fn(),
  useDeleteUser: vi.fn(),
}));

vi.mock('../contexts/ToastContext', () => ({
  useToastContext: vi.fn(() => ({
    toasts: [],
    success: vi.fn(),
    error: vi.fn(),
    removeToast: vi.fn(),
    clearAllToasts: vi.fn(),
  })),
  ToastProvider: ({ children }: { children: React.ReactNode }) => children,
}));

// Mock media query hook
vi.mock('../hooks/useMediaQuery', () => ({
  useIsDesktop: vi.fn(() => true),
  useMediaQuery: vi.fn(() => true),
}));

import { useDeleteUser, useUsers } from '../hooks/useUserQueries';

// Test wrapper with all required providers
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return (
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <ToastProvider>{children}</ToastProvider>
      </QueryClientProvider>
    </BrowserRouter>
  );
};

describe('UsersPage', () => {
  const mockUsers = [
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

  const mockUseUsers = {
    data: mockUsers,
    allData: mockUsers,
    isLoading: false,
    error: null,
    refetch: vi.fn(),
    filterConfig: {
      searchTerm: '',
      statusFilter: '',
    },
    sortConfig: { key: 'name', direction: 'asc' as const },
    pagination: {
      currentPage: 1,
      totalPages: 1,
      pageSize: 10,
      totalRecords: 2,
    },
    updateFilters: vi.fn(),
    handleSort: vi.fn(),
    goToPage: vi.fn(),
    changePageSize: vi.fn(),
  };

  const mockUseDeleteUser = {
    mutate: vi.fn(),
    mutateAsync: vi.fn(),
    isPending: false,
    error: null,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    mockSearchParams.forEach((_, key) => mockSearchParams.delete(key)); // Clear search params
    (useUsers as ReturnType<typeof vi.fn>).mockReturnValue(mockUseUsers);
    (useDeleteUser as ReturnType<typeof vi.fn>).mockReturnValue(mockUseDeleteUser);
  });

  describe('Rendering', () => {
    it('renders users page with user table', () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    });

    it('renders search and filter components', () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // Search input should be present (it has role="searchbox")
      expect(screen.getByRole('searchbox')).toBeInTheDocument();

      // Filter dropdown should be present
      expect(screen.getByLabelText('Filter users by status')).toBeInTheDocument();
    });

    it('renders pagination component', () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // Pagination should show results info
      expect(screen.getByText(/showing/i)).toBeInTheDocument();
    });
  });

  describe('Loading State', () => {
    it('shows loading spinner when data is loading', () => {
      (useUsers as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUsers,
        isLoading: true,
      });

      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      expect(screen.getByText('Loading...')).toBeInTheDocument();
    });

    it('hides user table when loading', () => {
      (useUsers as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUsers,
        isLoading: true,
      });

      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
    });
  });

  describe('Search and Filtering', () => {
    it('calls updateFilters when search term changes', async () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      const searchInput = screen.getByRole('searchbox');
      fireEvent.change(searchInput, { target: { value: 'John' } });

      await waitFor(() => {
        expect(mockUseUsers.updateFilters).toHaveBeenCalledWith(
          expect.objectContaining({
            searchTerm: 'John',
          })
        );
      });
    });

    it('calls updateFilters when status filter changes', async () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      const filterSelect = screen.getByLabelText('Filter users by status');
      fireEvent.change(filterSelect, { target: { value: 'Active' } });

      await waitFor(() => {
        expect(mockUseUsers.updateFilters).toHaveBeenCalledWith(
          expect.objectContaining({
            statusFilter: 'Active',
          })
        );
      });
    });
  });

  describe('Sorting', () => {
    it('calls handleSort when table header is clicked', async () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // Find and click a sortable header
      const nameHeader = screen.getByText('Name');
      fireEvent.click(nameHeader);

      await waitFor(() => {
        expect(mockUseUsers.handleSort).toHaveBeenCalledWith('name');
      });
    });
  });

  describe('Pagination', () => {
    it('calls goToPage when page changes', async () => {
      const mockPagination = {
        currentPage: 1,
        totalPages: 3,
        pageSize: 10,
        totalRecords: 25,
      };

      (useUsers as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUsers,
        pagination: mockPagination,
      });

      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // Find and click next page button
      const nextButton = screen.getByText('Next');
      fireEvent.click(nextButton);

      await waitFor(() => {
        expect(mockUseUsers.goToPage).toHaveBeenCalledWith(2);
      });
    });

    it('calls changePageSize when page size changes', async () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // Find page size selector by id
      const pageSizeSelect = screen.getByDisplayValue('10');
      fireEvent.change(pageSizeSelect, { target: { value: '20' } });

      await waitFor(() => {
        expect(mockUseUsers.changePageSize).toHaveBeenCalledWith(20);
      });
    });
  });

  describe('User Deletion', () => {
    it('opens confirmation modal when delete button is clicked', async () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // Find and click delete button for first user
      const deleteButtons = screen.getAllByText('Delete');
      fireEvent.click(deleteButtons[0]);

      await waitFor(() => {
        expect(screen.getByRole('dialog')).toBeInTheDocument();
        expect(screen.getByText(/are you sure you want to delete/i)).toBeInTheDocument();
      });
    });

    it('closes confirmation modal when cancel is clicked', async () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // Open modal
      const deleteButtons = screen.getAllByText('Delete');
      fireEvent.click(deleteButtons[0]);

      await waitFor(() => {
        expect(screen.getByRole('dialog')).toBeInTheDocument();
      });

      // Click cancel
      const cancelButton = screen.getByText('Cancel');
      fireEvent.click(cancelButton);

      await waitFor(() => {
        expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
      });
    });

    it('calls delete mutation when confirmed', async () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // Open modal
      const deleteButtons = screen.getAllByText('Delete');
      fireEvent.click(deleteButtons[0]);

      await waitFor(() => {
        expect(screen.getByRole('dialog')).toBeInTheDocument();
      });

      // Click confirm - look for the Delete button in the modal specifically
      const confirmButton = screen.getByRole('button', { name: /delete action/i });
      fireEvent.click(confirmButton);

      await waitFor(() => {
        expect(mockUseDeleteUser.mutateAsync).toHaveBeenCalledWith('1');
      });
    });
  });

  describe('Navigation', () => {
    it('navigates to user detail when view button is clicked', async () => {
      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      const viewButtons = screen.getAllByText('View');
      fireEvent.click(viewButtons[0]);

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/users/1', expect.any(Object));
      });
    });
  });

  describe('URL State Synchronization', () => {
    it('initializes filters from URL parameters', () => {
      // Set up URL parameters
      mockSearchParams.set('search', 'john');
      mockSearchParams.set('status', 'Active');
      mockSearchParams.set('page', '2');

      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      // The component should have called updateFilters with URL params
      expect(mockUseUsers.updateFilters).toHaveBeenCalled();
    });
  });

  describe('Error Handling', () => {
    it('displays error message when there is an error', () => {
      (useUsers as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUsers,
        error: new Error('Failed to fetch users'),
      });

      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      expect(screen.getByText('Error Loading Users')).toBeInTheDocument();
    });

    it('shows retry button when there is an error', () => {
      (useUsers as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUsers,
        error: new Error('Failed to fetch users'),
      });

      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      const retryButton = screen.getByText('Try Again');
      expect(retryButton).toBeInTheDocument();

      fireEvent.click(retryButton);
      expect(mockUseUsers.refetch).toHaveBeenCalled();
    });
  });

  describe('Empty State', () => {
    it('shows empty state when no users', () => {
      (useUsers as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUsers,
        data: [],
        allData: [],
      });

      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      expect(screen.getByText('No data found')).toBeInTheDocument();
    });

    it('shows appropriate message for filtered results', () => {
      (useUsers as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUsers,
        data: [],
        allData: mockUsers,
        filterConfig: {
          searchTerm: 'nonexistent',
          statusFilter: '',
        },
      });

      render(
        <TestWrapper>
          <UsersPage />
        </TestWrapper>
      );

      expect(screen.getByText('Try adjusting your search or filters')).toBeInTheDocument();
    });
  });
});
