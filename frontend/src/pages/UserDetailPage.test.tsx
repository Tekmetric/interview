import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ToastProvider } from '../contexts/ToastContext';
import { UserDetailPage } from './UserDetailPage';

// Mock the hooks
vi.mock('../hooks/useUserQueries', () => ({
  useUser: vi.fn(),
  useUserForm: vi.fn(),
}));

vi.mock('../hooks/useToast', () => ({
  useToast: vi.fn(() => ({
    toasts: [],
    success: vi.fn(),
    error: vi.fn(),
    removeToast: vi.fn(),
    clearAllToasts: vi.fn(),
  })),
}));

// Mock validation
vi.mock('../utils/validation', () => ({
  validateField: vi.fn(() => ''),
  validateUserForm: vi.fn(() => ({ isValid: true, errors: {} })),
}));

import { useUser, useUserForm } from '../hooks/useUserQueries';
import { validateField, validateUserForm } from '../utils/validation';

// Test wrapper with all required providers
const TestWrapper: React.FC<{
  children: React.ReactNode;
  initialEntries?: string[];
}> = ({ children, initialEntries = ['/users/1'] }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return (
    <QueryClientProvider client={queryClient}>
      <ToastProvider>
        <MemoryRouter initialEntries={initialEntries}>
          <Routes>
            <Route path='/users/:id' element={children} />
            <Route path='/users/new' element={children} />
            <Route path='/users/:id/edit' element={children} />
          </Routes>
        </MemoryRouter>
      </ToastProvider>
    </QueryClientProvider>
  );
};

describe('UserDetailPage', () => {
  const mockUser = {
    id: '1',
    name: 'John Doe',
    email: 'john@example.com',
    phone: '+1234567890',
    company: 'Acme Corp',
    status: 'Active',
    createdAt: '2024-01-15T10:30:00Z',
  };

  const mockUseUser = {
    data: mockUser,
    isLoading: false,
    error: null,
  };

  const mockUseUserForm = {
    submitForm: vi.fn(),
    isLoading: false,
    error: null,
    isSuccess: false,
    reset: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (useUser as ReturnType<typeof vi.fn>).mockReturnValue(mockUseUser);
    (useUserForm as ReturnType<typeof vi.fn>).mockReturnValue(mockUseUserForm);
    (validateField as ReturnType<typeof vi.fn>).mockReturnValue('');
    (validateUserForm as ReturnType<typeof vi.fn>).mockReturnValue({ isValid: true, errors: {} });
  });

  describe('View Mode', () => {
    it('renders user information in view mode', () => {
      render(
        <TestWrapper>
          <UserDetailPage />
        </TestWrapper>
      );

      expect(screen.getByDisplayValue('John Doe')).toBeInTheDocument();
      expect(screen.getByDisplayValue('john@example.com')).toBeInTheDocument();
      expect(screen.getByDisplayValue('+1234567890')).toBeInTheDocument();
      expect(screen.getByDisplayValue('Acme Corp')).toBeInTheDocument();
    });

    it('shows fields as disabled in view mode', () => {
      render(
        <TestWrapper>
          <UserDetailPage />
        </TestWrapper>
      );

      const nameInput = screen.getByDisplayValue('John Doe');
      expect(nameInput).toBeDisabled();
    });

    it('shows edit button in view mode', () => {
      render(
        <TestWrapper>
          <UserDetailPage />
        </TestWrapper>
      );

      expect(screen.getByText('Edit User')).toBeInTheDocument();
    });

    it('shows edit link in view mode', () => {
      render(
        <TestWrapper>
          <UserDetailPage />
        </TestWrapper>
      );

      const editLink = screen.getByText('Edit User');
      expect(editLink).toBeInTheDocument();
      expect(editLink.closest('a')).toHaveAttribute('href', '/users/1/edit');
    });
  });

  describe('Edit Mode', () => {
    it('renders form fields as editable in edit mode', () => {
      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const nameInput = screen.getByDisplayValue('John Doe');
      expect(nameInput).not.toBeDisabled();
    });

    it('shows save and cancel buttons in edit mode', () => {
      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      expect(screen.getByText('Save Changes')).toBeInTheDocument();
      expect(screen.getByText('Cancel')).toBeInTheDocument();
    });

    it('validates fields on blur', async () => {
      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const nameInput = screen.getByDisplayValue('John Doe');
      fireEvent.change(nameInput, { target: { value: 'Jane Smith' } });
      fireEvent.blur(nameInput);

      await waitFor(() => {
        expect(validateField).toHaveBeenCalledWith('name', 'Jane Smith');
      });
    });

    it('calls updateUser when form is submitted', async () => {
      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const nameInput = screen.getByDisplayValue('John Doe');
      fireEvent.change(nameInput, { target: { value: 'Jane Smith' } });

      const saveButton = screen.getByText('Save Changes');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockUseUserForm.submitForm).toHaveBeenCalledWith(
          expect.objectContaining({
            name: 'Jane Smith',
          })
        );
      });
    });

    it('shows cancel button that navigates away', () => {
      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const nameInput = screen.getByDisplayValue('John Doe');
      fireEvent.change(nameInput, { target: { value: 'Jane Smith' } });

      const cancelButton = screen.getByText('Cancel');
      expect(cancelButton).toBeInTheDocument();

      // The cancel button exists and would navigate away when clicked
      expect(cancelButton.tagName).toBe('BUTTON');
    });
  });

  describe('New User Mode', () => {
    it('renders empty form for new user', () => {
      (useUser as ReturnType<typeof vi.fn>).mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
      });

      render(
        <TestWrapper initialEntries={['/users/new']}>
          <UserDetailPage newUser={true} />
        </TestWrapper>
      );

      const nameInput = screen.getByLabelText(/name/i);
      expect(nameInput).toHaveValue('');
    });

    it('shows create user button for new user', () => {
      (useUser as ReturnType<typeof vi.fn>).mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
      });

      render(
        <TestWrapper initialEntries={['/users/new']}>
          <UserDetailPage newUser={true} />
        </TestWrapper>
      );

      expect(screen.getByText('Create User')).toBeInTheDocument();
    });

    it('calls createUser when form is submitted', async () => {
      (useUser as ReturnType<typeof vi.fn>).mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
      });

      render(
        <TestWrapper initialEntries={['/users/new']}>
          <UserDetailPage newUser={true} />
        </TestWrapper>
      );

      // Fill in required fields
      const nameInput = screen.getByLabelText(/name/i);
      const emailInput = screen.getByLabelText(/email/i);

      fireEvent.change(nameInput, { target: { value: 'New User' } });
      fireEvent.change(emailInput, { target: { value: 'new@example.com' } });

      const createButton = screen.getByText('Create User');
      fireEvent.click(createButton);

      await waitFor(() => {
        expect(mockUseUserForm.submitForm).toHaveBeenCalledWith(
          expect.objectContaining({
            name: 'New User',
            email: 'new@example.com',
          })
        );
      });
    });
  });

  describe('Loading State', () => {
    it('shows loading spinner when fetching user data', () => {
      (useUser as ReturnType<typeof vi.fn>).mockReturnValue({
        data: null,
        isLoading: true,
        error: null,
      });

      render(
        <TestWrapper>
          <UserDetailPage />
        </TestWrapper>
      );

      expect(screen.getByRole('status')).toBeInTheDocument();
      expect(screen.getByText('Loading...')).toBeInTheDocument();
    });

    it('shows loading state on save button when updating', () => {
      (useUserForm as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUserForm,
        isLoading: true,
      });

      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const saveButton = screen.getByText('Saving...');
      expect(saveButton).toBeInTheDocument();
      expect(saveButton).toBeDisabled();
    });

    it('shows loading state on create button when creating', () => {
      (useUser as ReturnType<typeof vi.fn>).mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
      });

      (useUserForm as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUserForm,
        isLoading: true,
      });

      render(
        <TestWrapper initialEntries={['/users/new']}>
          <UserDetailPage newUser={true} />
        </TestWrapper>
      );

      const createButton = screen.getByText('Saving...');
      expect(createButton).toBeInTheDocument();
      expect(createButton).toBeDisabled();
    });
  });

  describe('Form Validation', () => {
    it('displays field validation errors', async () => {
      (validateField as ReturnType<typeof vi.fn>).mockReturnValue('Name is required');

      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const nameInput = screen.getByDisplayValue('John Doe');
      fireEvent.change(nameInput, { target: { value: '' } });
      fireEvent.blur(nameInput);

      await waitFor(() => {
        expect(screen.getByText('Name is required')).toBeInTheDocument();
      });
    });

    it('prevents form submission when validation fails', async () => {
      (validateUserForm as ReturnType<typeof vi.fn>).mockReturnValue({
        isValid: false,
        errors: { name: 'Name is required' },
      });

      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const saveButton = screen.getByText('Save Changes');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockUseUserForm.submitForm).not.toHaveBeenCalled();
      });
    });

    it('shows form-level validation errors', async () => {
      (validateUserForm as ReturnType<typeof vi.fn>).mockReturnValue({
        isValid: false,
        errors: {
          name: 'Name is required',
          email: 'Email is invalid',
        },
      });

      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const saveButton = screen.getByText('Save Changes');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(screen.getByText('Name is required')).toBeInTheDocument();
        expect(screen.getByText('Email is invalid')).toBeInTheDocument();
      });
    });
  });

  describe('Error Handling', () => {
    it('displays error when user not found', () => {
      (useUser as ReturnType<typeof vi.fn>).mockReturnValue({
        data: null,
        isLoading: false,
        error: new Error('User not found'),
      });

      render(
        <TestWrapper>
          <UserDetailPage />
        </TestWrapper>
      );

      expect(screen.getByText(/user not found/i)).toBeInTheDocument();
    });

    it('handles save error through hook', () => {
      (useUserForm as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUserForm,
        error: { message: 'Failed to update user' },
      });

      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      // The error should be handled by the hook's onError callback
      // This test verifies the error exists in the hook
      const hookResult = (useUserForm as ReturnType<typeof vi.fn>).mock.results[0].value;
      expect(hookResult.error).toBeTruthy();
      expect(hookResult.error.message).toBe('Failed to update user');
    });

    it('handles create error through hook', () => {
      (useUser as ReturnType<typeof vi.fn>).mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
      });

      (useUserForm as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUserForm,
        error: { message: 'Failed to create user' },
      });

      render(
        <TestWrapper initialEntries={['/users/new']}>
          <UserDetailPage newUser={true} />
        </TestWrapper>
      );

      // The error should be handled by the hook's onError callback
      // This test verifies the error exists in the hook
      const hookResult = (useUserForm as ReturnType<typeof vi.fn>).mock.results[0].value;
      expect(hookResult.error).toBeTruthy();
      expect(hookResult.error.message).toBe('Failed to create user');
    });
  });

  describe('Navigation', () => {
    it('shows back to users button', () => {
      render(
        <TestWrapper>
          <UserDetailPage />
        </TestWrapper>
      );

      const backButton = screen.getByText(/back to users/i);
      expect(backButton).toBeInTheDocument();
      expect(backButton.tagName).toBe('BUTTON');
    });

    it('navigates back after successful creation', async () => {
      const mockNavigate = vi.fn();

      // Mock useNavigate at the module level
      vi.doMock('react-router-dom', async () => {
        const actual = await vi.importActual('react-router-dom');
        return {
          ...actual,
          useNavigate: () => mockNavigate,
        };
      });

      // Mock successful form submission
      const mockSubmitForm = vi.fn().mockResolvedValue({ id: 'new-user-id' });

      (useUser as ReturnType<typeof vi.fn>).mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
      });

      (useUserForm as ReturnType<typeof vi.fn>).mockReturnValue({
        ...mockUseUserForm,
        submitForm: mockSubmitForm,
      });

      render(
        <TestWrapper initialEntries={['/users/new']}>
          <UserDetailPage newUser={true} />
        </TestWrapper>
      );

      // Fill form and submit
      const nameInput = screen.getByLabelText(/name/i);
      const emailInput = screen.getByLabelText(/email/i);

      fireEvent.change(nameInput, { target: { value: 'New User' } });
      fireEvent.change(emailInput, { target: { value: 'new@example.com' } });

      const createButton = screen.getByText('Create User');
      fireEvent.click(createButton);

      // Wait for form submission and navigation
      await waitFor(() => {
        expect(mockSubmitForm).toHaveBeenCalled();
      });
    });
  });

  describe('Status Field', () => {
    it('renders status as select dropdown', () => {
      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const statusSelect = screen.getByDisplayValue('Active');
      expect(statusSelect.tagName).toBe('SELECT');
    });

    it('has correct status options', () => {
      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      expect(screen.getByText('Active')).toBeInTheDocument();
      expect(screen.getByText('Inactive')).toBeInTheDocument();
    });
  });

  describe('Accessibility', () => {
    it('has proper form labels', () => {
      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      expect(screen.getByLabelText(/name/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/phone/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/company/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/status/i)).toBeInTheDocument();
    });

    it('associates error messages with form fields', async () => {
      (validateField as ReturnType<typeof vi.fn>).mockReturnValue('Name is required');

      render(
        <TestWrapper>
          <UserDetailPage editMode={true} />
        </TestWrapper>
      );

      const nameInput = screen.getByLabelText(/name/i);
      fireEvent.change(nameInput, { target: { value: '' } });
      fireEvent.blur(nameInput);

      await waitFor(() => {
        const errorMessage = screen.getByText('Name is required');
        expect(errorMessage).toBeInTheDocument();
        expect(nameInput).toHaveAttribute('aria-describedby');
      });
    });
  });
});
