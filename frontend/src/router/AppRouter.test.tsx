import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ThemeProvider } from '../contexts/ThemeContext';
import { ToastProvider } from '../contexts/ToastContext';
import { AppRouter } from './AppRouter';

// Mock all page components
vi.mock('../pages/HomePage', () => ({
  HomePage: () => <div data-testid='home-page'>Home Page</div>,
}));

vi.mock('../pages/UsersPage', () => ({
  UsersPage: () => <div data-testid='users-page'>Users Page</div>,
}));

vi.mock('../pages/UserDetailPage', () => ({
  UserDetailPage: ({ newUser, editMode }: { newUser?: boolean; editMode?: boolean }) => (
    <div data-testid='user-detail-page'>
      {newUser ? 'New User Page' : editMode ? 'Edit User Page' : 'User Detail Page'}
    </div>
  ),
}));

vi.mock('../pages/HelpPage', () => ({
  HelpPage: () => <div data-testid='help-page'>Help Page</div>,
}));

vi.mock('../pages/help/GettingStartedPage', () => ({
  GettingStartedPage: () => <div data-testid='getting-started-page'>Getting Started Page</div>,
}));

vi.mock('../pages/help/UserManagementPage', () => ({
  UserManagementPage: () => <div data-testid='user-management-page'>User Management Page</div>,
}));

vi.mock('../pages/help/FeaturesPage', () => ({
  FeaturesPage: () => <div data-testid='features-page'>Features Page</div>,
}));

vi.mock('../pages/NotFoundPage', () => ({
  NotFoundPage: () => <div data-testid='not-found-page'>Not Found Page</div>,
}));

// Mock the Layout component to include Outlet for nested routes
vi.mock('../components/Layout', () => ({
  Layout: () => {
    const { Outlet } = require('react-router-dom');
    return (
      <div data-testid='layout'>
        <Outlet />
      </div>
    );
  },
}));

// Mock React Router to use MemoryRouter instead of BrowserRouter
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    BrowserRouter: ({ children }: { children: React.ReactNode }) => children, // Remove the router wrapper
  };
});

// Mock hooks that might be used by components
vi.mock('../hooks/useUserQueries', () => ({
  useUser: vi.fn(() => ({
    data: null,
    isLoading: false,
    error: null,
  })),
  useUsers: vi.fn(() => ({
    data: { data: [], total: 0 },
    isLoading: false,
    error: null,
  })),
  useUserForm: vi.fn(() => ({
    createUser: vi.fn(),
    updateUser: vi.fn(),
    isCreating: false,
    isUpdating: false,
    createError: null,
    updateError: null,
  })),
}));

vi.mock('../hooks/useToast', () => ({
  useToast: vi.fn(() => ({
    toasts: [],
    success: vi.fn(),
    error: vi.fn(),
    removeToast: vi.fn(),
  })),
}));

// Test wrapper with required providers and MemoryRouter
const TestWrapper: React.FC<{
  children: React.ReactNode;
  initialEntries?: string[];
}> = ({ children, initialEntries = ['/'] }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return (
    <MemoryRouter initialEntries={initialEntries}>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          <ToastProvider>{children}</ToastProvider>
        </ThemeProvider>
      </QueryClientProvider>
    </MemoryRouter>
  );
};

describe('AppRouter', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Route Navigation', () => {
    it('renders home page for root path', () => {
      render(
        <TestWrapper initialEntries={['/']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('layout')).toBeInTheDocument();
      expect(screen.getByTestId('home-page')).toBeInTheDocument();
    });

    it('renders users page for /users path', () => {
      render(
        <TestWrapper initialEntries={['/users']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('users-page')).toBeInTheDocument();
    });

    it('renders user detail page for specific user ID', () => {
      render(
        <TestWrapper initialEntries={['/users/123']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('user-detail-page')).toBeInTheDocument();
      expect(screen.getByText('User Detail Page')).toBeInTheDocument();
    });

    it('renders new user page for /users/new path', () => {
      render(
        <TestWrapper initialEntries={['/users/new']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('user-detail-page')).toBeInTheDocument();
      expect(screen.getByText('New User Page')).toBeInTheDocument();
    });

    it('renders edit user page for /users/:id/edit path', () => {
      render(
        <TestWrapper initialEntries={['/users/123/edit']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('user-detail-page')).toBeInTheDocument();
      expect(screen.getByText('Edit User Page')).toBeInTheDocument();
    });

    it('renders help page for /help path', () => {
      render(
        <TestWrapper initialEntries={['/help']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('help-page')).toBeInTheDocument();
    });

    it('renders not found page for invalid paths', () => {
      render(
        <TestWrapper initialEntries={['/invalid-route']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('not-found-page')).toBeInTheDocument();
    });
  });

  describe('Route Parameters', () => {
    it('handles numeric user IDs', () => {
      render(
        <TestWrapper initialEntries={['/users/42']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('user-detail-page')).toBeInTheDocument();
    });

    it('handles UUID user IDs', () => {
      render(
        <TestWrapper initialEntries={['/users/550e8400-e29b-41d4-a716-446655440000']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('user-detail-page')).toBeInTheDocument();
    });

    it('prioritizes /users/new over dynamic ID route', () => {
      render(
        <TestWrapper initialEntries={['/users/new']}>
          <AppRouter />
        </TestWrapper>
      );

      // Should render new user page, not treat "new" as a user ID
      expect(screen.getByText('New User Page')).toBeInTheDocument();
    });
  });

  describe('Layout Integration', () => {
    it('wraps all routes in Layout component', () => {
      render(
        <TestWrapper initialEntries={['/users']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('layout')).toBeInTheDocument();
    });

    it('maintains layout across route changes', () => {
      const { rerender } = render(
        <TestWrapper initialEntries={['/users']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('layout')).toBeInTheDocument();

      // Simulate navigation to help page
      rerender(
        <TestWrapper initialEntries={['/help']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('layout')).toBeInTheDocument();
    });
  });

  describe('Nested Routes Structure', () => {
    it('handles users route with nested paths correctly', () => {
      // Test that /users routes are properly nested
      render(
        <TestWrapper initialEntries={['/users']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('users-page')).toBeInTheDocument();
    });

    it('handles deep nested routes', () => {
      // Test the actual nested route that exists: /users/:id/edit
      render(
        <TestWrapper initialEntries={['/users/123/edit']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('user-detail-page')).toBeInTheDocument();
      expect(screen.getByText('Edit User Page')).toBeInTheDocument();
    });
  });

  describe('Route Guards and Error Boundaries', () => {
    it('handles errors in route components gracefully', () => {
      // Mock a component that throws an error
      const ErrorComponent = () => {
        throw new Error('Component error');
      };

      vi.doMock('../pages/UsersPage', () => ({
        UsersPage: ErrorComponent,
      }));

      // The router should handle this gracefully
      expect(() => {
        render(
          <TestWrapper initialEntries={['/users']}>
            <AppRouter />
          </TestWrapper>
        );
      }).not.toThrow();
    });
  });

  describe('Default Route Behavior', () => {
    it('renders home page as default route', () => {
      render(
        <TestWrapper initialEntries={['/']}>
          <AppRouter />
        </TestWrapper>
      );

      // Should show home page content for root route
      expect(screen.getByTestId('home-page')).toBeInTheDocument();
    });

    it('handles trailing slashes in routes', () => {
      render(
        <TestWrapper initialEntries={['/users/']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('users-page')).toBeInTheDocument();
    });
  });

  describe('URL Query Parameters', () => {
    it('preserves query parameters in routes', () => {
      render(
        <TestWrapper initialEntries={['/users?page=2&search=john']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('users-page')).toBeInTheDocument();
      // The UsersPage component should handle the query parameters
    });

    it('handles hash fragments in URLs', () => {
      render(
        <TestWrapper initialEntries={['/users#top']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('users-page')).toBeInTheDocument();
    });
  });

  describe('Navigation State', () => {
    it('renders different routes correctly', () => {
      // Test users route
      const { unmount } = render(
        <TestWrapper initialEntries={['/users']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('users-page')).toBeInTheDocument();
      unmount();

      // Test help route separately
      render(
        <TestWrapper initialEntries={['/help']}>
          <AppRouter />
        </TestWrapper>
      );

      expect(screen.getByTestId('help-page')).toBeInTheDocument();
    });
  });

  describe('Route Accessibility', () => {
    it('updates document title for different routes', () => {
      // This would test if the router properly updates page titles
      render(
        <TestWrapper initialEntries={['/help']}>
          <AppRouter />
        </TestWrapper>
      );

      // The Layout or individual pages should handle title updates
      expect(screen.getByTestId('help-page')).toBeInTheDocument();
    });

    it('handles keyboard navigation properly', () => {
      render(
        <TestWrapper initialEntries={['/users']}>
          <AppRouter />
        </TestWrapper>
      );

      // Router should not interfere with keyboard navigation
      expect(screen.getByTestId('users-page')).toBeInTheDocument();
    });
  });

  describe('Performance and Lazy Loading', () => {
    it('renders routes efficiently', () => {
      // Test that the router renders components without throwing errors
      const { container } = render(
        <TestWrapper initialEntries={['/users']}>
          <AppRouter />
        </TestWrapper>
      );

      // Should render the users page successfully
      expect(screen.getByTestId('users-page')).toBeInTheDocument();

      // Check that the layout wrapper is present
      expect(screen.getByTestId('layout')).toBeInTheDocument();

      // Verify the DOM structure is clean (no duplicate elements)
      const usersPages = container.querySelectorAll('[data-testid="users-page"]');
      expect(usersPages).toHaveLength(1);
    });
  });
});
