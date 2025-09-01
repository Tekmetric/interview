import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ThemeProvider } from '../contexts/ThemeContext';
import { NotFoundPage } from './NotFoundPage';

// Mock the theme context
const mockThemeContext = {
  theme: 'system' as const,
  setTheme: vi.fn(),
  actualTheme: 'light' as const,
};

// Mock react-router-dom navigate function
const mockNavigate = vi.fn();

vi.mock('../contexts/ThemeContext', () => ({
  ThemeProvider: ({ children }: { children: React.ReactNode }) => children,
  useTheme: () => mockThemeContext,
}));

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useLocation: () => ({ pathname: '/invalid-path' }),
  };
});

// Test wrapper to provide routing context
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>
    <ThemeProvider>{children}</ThemeProvider>
  </BrowserRouter>
);

describe('NotFoundPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders 404 error message', () => {
    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    expect(screen.getByText('404')).toBeInTheDocument();
    expect(screen.getByText('Page Not Found')).toBeInTheDocument();
    expect(
      screen.getByText(/the page you're looking for doesn't exist or has been moved/i)
    ).toBeInTheDocument();
  });

  it('displays navigation suggestions', () => {
    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    expect(screen.getByText('Try these pages instead:')).toBeInTheDocument();
    
    // Use getAllByRole to handle multiple links with similar names
    const homeLinks = screen.getAllByRole('link', { name: /home/i });
    const usersLinks = screen.getAllByRole('link', { name: /users/i });
    const helpLinks = screen.getAllByRole('link', { name: /help/i });
    const newUserLinks = screen.getAllByRole('link', { name: /new user/i });

    expect(homeLinks.length).toBeGreaterThan(0);
    expect(usersLinks.length).toBeGreaterThan(0);
    expect(helpLinks.length).toBeGreaterThan(0);
    expect(newUserLinks.length).toBeGreaterThan(0);
  });

  it('has correct navigation links', () => {
    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    // Use more specific selectors by checking aria-label
    const homeLink = screen.getByRole('link', { name: 'Home - Return to the main dashboard' });
    const usersLink = screen.getByRole('link', { name: 'Users - Manage user accounts' });
    const helpLink = screen.getByRole('link', { name: 'Help - Get help and documentation' });
    const newUserLink = screen.getByRole('link', { name: 'New User - Create a new user account' });

    expect(homeLink).toHaveAttribute('href', '/');
    expect(usersLink).toHaveAttribute('href', '/users');
    expect(helpLink).toHaveAttribute('href', '/help');
    expect(newUserLink).toHaveAttribute('href', '/users/new');
  });

  it('displays suggestion descriptions', () => {
    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    expect(screen.getByText('Return to the main dashboard')).toBeInTheDocument();
    expect(screen.getByText('Manage user accounts')).toBeInTheDocument();
    expect(screen.getByText('Get help and documentation')).toBeInTheDocument();
    expect(screen.getByText('Create a new user account')).toBeInTheDocument();
  });

  it('has a go back button that calls navigate(-1)', async () => {
    const user = userEvent.setup();

    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    const goBackButton = screen.getByRole('button', { name: /go back/i });
    expect(goBackButton).toBeInTheDocument();

    await user.click(goBackButton);
    expect(mockNavigate).toHaveBeenCalledWith(-1);
  });

  it('renders action buttons section', () => {
    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    expect(screen.getByText('Quick Navigation')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /go back/i })).toBeInTheDocument();
    
    // Use specific aria-label to target the correct home button
    const homeButton = screen.getByRole('link', { name: 'Go to home page' });
    expect(homeButton).toBeInTheDocument();
  });

  it('has home button with correct link', () => {
    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    // Check for the specific Home button in the quick actions (the one with "Go to home page" aria-label)
    const homeButton = screen.getByRole('link', { name: 'Go to home page' });
    expect(homeButton).toHaveAttribute('href', '/');
  });

  it('displays route not found section', () => {
    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    expect(screen.getByText('Route Not Found')).toBeInTheDocument();
    expect(screen.getByText('Attempted path:')).toBeInTheDocument();
  });

  it('displays quick navigation links', () => {
    render(
      <TestWrapper>
        <NotFoundPage />
      </TestWrapper>
    );

    expect(screen.getByRole('link', { name: /getting started/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /user management/i })).toBeInTheDocument();
  });
});
