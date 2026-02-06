import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';

import { ThemeProvider } from '../../contexts/ThemeContext';
import { UserManagementPage } from './UserManagementPage';

// Mock the theme context
const mockThemeContext = {
  theme: 'system' as const,
  setTheme: vi.fn(),
  actualTheme: 'light' as const,
};

vi.mock('../../contexts/ThemeContext', () => ({
  ThemeProvider: ({ children }: { children: React.ReactNode }) => children,
  useTheme: () => mockThemeContext,
}));

// Test wrapper to provide routing context
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>
    <ThemeProvider>{children}</ThemeProvider>
  </BrowserRouter>
);

describe('UserManagementPage', () => {
  it('renders main heading', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('User Management')).toBeInTheDocument();
  });

  it('displays description text', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(
      screen.getByText('Learn how to manage users in this dashboard application.')
    ).toBeInTheDocument();
  });

  it('renders overview section', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('Overview')).toBeInTheDocument();
    expect(
      screen.getByText(/the user management system allows you to view, search, and manage/i)
    ).toBeInTheDocument();
  });

  it('renders viewing users section', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('Viewing Users')).toBeInTheDocument();
    expect(
      screen.getByText(
        'The Users page displays all users in a table format with the following information:'
      )
    ).toBeInTheDocument();
  });

  it('displays user information fields', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('Name:')).toBeInTheDocument();
    expect(screen.getByText('Email:')).toBeInTheDocument();
    expect(screen.getByText('Phone:')).toBeInTheDocument();
    expect(screen.getByText('Company:')).toBeInTheDocument();
    expect(screen.getByText('Status:')).toBeInTheDocument();
    expect(screen.getByText('Created:')).toBeInTheDocument();

    expect(screen.getByText(/user's full name/i)).toBeInTheDocument();
    expect(screen.getByText('Contact email address')).toBeInTheDocument();
    expect(screen.getByText(/phone number \(when available\)/i)).toBeInTheDocument();
    expect(screen.getByText('Associated company')).toBeInTheDocument();
    expect(screen.getByText(/current status \(active\/inactive\)/i)).toBeInTheDocument();
    expect(screen.getByText('When the user was added')).toBeInTheDocument();
  });

  it('renders user details section', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('User Details')).toBeInTheDocument();
    expect(
      screen.getByText(
        /click on the view button in any user's row to view their detailed profile page/i
      )
    ).toBeInTheDocument();
  });

  it('renders search and filtering section', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('Search and Filtering')).toBeInTheDocument();
    expect(screen.getByText('Search Users')).toBeInTheDocument();
    expect(screen.getByText('Filter by Status')).toBeInTheDocument();
  });

  it('displays search and filtering descriptions', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(
      screen.getByText(/use the search bar to find users by name, email, or company/i)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/use the status dropdown to show only active or inactive users/i)
    ).toBeInTheDocument();
  });

  it('renders sorting and pagination section', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('Sorting and Pagination')).toBeInTheDocument();
    expect(
      screen.getByText('Click on any column header to sort by that field')
    ).toBeInTheDocument();
    expect(screen.getByText('Click again to reverse the sort order')).toBeInTheDocument();
    expect(
      screen.getByText('Use pagination controls at the bottom to navigate through pages')
    ).toBeInTheDocument();
    expect(screen.getByText(/adjust page size using the dropdown/i)).toBeInTheDocument();
  });

  it('displays navigation tip', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('Navigation Tip:')).toBeInTheDocument();
    expect(
      screen.getByText(/each user has a unique url that you can bookmark or share/i)
    ).toBeInTheDocument();
  });

  it('renders quick tips section', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(screen.getByText('Quick Tips:')).toBeInTheDocument();
  });

  it('displays quick tips content', () => {
    render(
      <TestWrapper>
        <UserManagementPage />
      </TestWrapper>
    );

    expect(
      screen.getByText(/combine search with status filters for more precise results/i)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/sort by "created" to see the newest or oldest users first/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/bookmark search results by copying the url/i)).toBeInTheDocument();
  });
});
