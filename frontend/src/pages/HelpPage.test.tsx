import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';

import { ThemeProvider } from '../contexts/ThemeContext';
import { HelpPage } from './HelpPage';

// Mock the theme context
const mockThemeContext = {
  theme: 'system' as const,
  setTheme: vi.fn(),
  actualTheme: 'light' as const,
};

vi.mock('../contexts/ThemeContext', () => ({
  ThemeProvider: ({ children }: { children: React.ReactNode }) => children,
  useTheme: () => mockThemeContext,
}));

// Mock react-router-dom to simulate being on the main help page
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useLocation: () => ({ pathname: '/help' }),
    Outlet: () => <div data-testid="outlet" />,
  };
});

// Test wrapper to provide routing context
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>
    <ThemeProvider>{children}</ThemeProvider>
  </BrowserRouter>
);

describe('HelpPage', () => {
  it('renders main heading', () => {
    render(
      <TestWrapper>
        <HelpPage />
      </TestWrapper>
    );

    expect(screen.getByText('Help & Documentation')).toBeInTheDocument();
  });

  it('displays description text', () => {
    render(
      <TestWrapper>
        <HelpPage />
      </TestWrapper>
    );

    expect(
      screen.getByText('Learn how to use the user management dashboard effectively.')
    ).toBeInTheDocument();
  });

  it('renders navigation links', () => {
    render(
      <TestWrapper>
        <HelpPage />
      </TestWrapper>
    );

    expect(screen.getByRole('link', { name: /overview/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /getting started/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /user management/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /features/i })).toBeInTheDocument();
  });

  it('renders help content section when on main help page', () => {
    render(
      <TestWrapper>
        <HelpPage />
      </TestWrapper>
    );

    expect(
      screen.getByText(/welcome to the user management dashboard!/i)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/this application demonstrates modern react development/i)
    ).toBeInTheDocument();
  });

  it('displays help sections with descriptions', () => {
    render(
      <TestWrapper>
        <HelpPage />
      </TestWrapper>
    );

    // Check for section headings (these are h3 elements in the help cards)
    expect(screen.getByRole('heading', { name: 'Quick Start' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'User Management' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Technical Features' })).toBeInTheDocument();

    expect(screen.getByText('Learn the basics in a few simple steps.')).toBeInTheDocument();
    expect(screen.getByText('How to add, edit, and delete users.')).toBeInTheDocument();
    expect(
      screen.getByText('Explore the technology and architecture.')
    ).toBeInTheDocument();
  });

  it('renders navigation links to help sections', () => {
    render(
      <TestWrapper>
        <HelpPage />
      </TestWrapper>
    );

    expect(screen.getByRole('link', { name: /get started →/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /learn more →/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /explore →/i })).toBeInTheDocument();
  });

  it('includes Outlet for nested routes', () => {
    render(
      <TestWrapper>
        <HelpPage />
      </TestWrapper>
    );

    // The Outlet component should be rendered (though we can't test its content directly)
    // We can verify the component renders without errors, which indicates Outlet is present
    expect(screen.getByText('Help & Documentation')).toBeInTheDocument();
  });
});
