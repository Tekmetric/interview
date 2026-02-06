import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';

import { ThemeProvider } from '../contexts/ThemeContext';
import { HomePage } from './HomePage';

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

// Test wrapper to provide routing context
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>
    <ThemeProvider>{children}</ThemeProvider>
  </BrowserRouter>
);

describe('HomePage', () => {
  it('renders main heading', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    expect(screen.getByText('User Management Dashboard')).toBeInTheDocument();
  });

  it('displays hero section with description', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    expect(screen.getByText(/comprehensive platform for managing users/i)).toBeInTheDocument();
  });

  it('renders navigation links in hero section', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    const manageUsersLinks = screen.getAllByRole('link', { name: /manage users/i });
    const getHelpLinks = screen.getAllByRole('link', { name: /get help/i });

    expect(manageUsersLinks[0]).toBeInTheDocument();
    expect(manageUsersLinks[0]).toHaveAttribute('href', '/users');

    expect(getHelpLinks[0]).toBeInTheDocument();
    expect(getHelpLinks[0]).toHaveAttribute('href', '/help');
  });

  it('displays feature cards', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    expect(screen.getByText('User Management')).toBeInTheDocument();
    expect(screen.getByText('Help & Documentation')).toBeInTheDocument();
    expect(screen.getByText(/complete crud operations/i)).toBeInTheDocument();
    expect(screen.getByText(/get help and learn about features/i)).toBeInTheDocument();
  });

  it('renders feature links', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    const featureLinks = screen.getAllByRole('link');

    // Should have links in hero + feature cards
    expect(featureLinks.length).toBeGreaterThan(2);

    // Check specific feature links
    const userManagementLink = featureLinks.find(link => link.getAttribute('href') === '/users');
    const helpLink = featureLinks.find(link => link.getAttribute('href') === '/help');

    expect(userManagementLink).toBeInTheDocument();
    expect(helpLink).toBeInTheDocument();
  });

  it('applies correct theme-based styling', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    // Find the hero section by its gradient classes
    const heroSection = document.querySelector('.bg-gradient-to-r');
    expect(heroSection).toBeInTheDocument();

    // In light theme, should have blue gradient
    expect(heroSection).toHaveClass('from-blue-600');
  });

  it('displays icons for features', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    // Icons are SVG elements, check they exist
    const icons = document.querySelectorAll('svg');
    expect(icons.length).toBeGreaterThan(0);
  });

  it('has proper semantic structure', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    // Check for proper heading hierarchy - component uses h1 and h3, not h2
    expect(screen.getByRole('heading', { level: 1 })).toBeInTheDocument();
    expect(screen.getAllByRole('heading', { level: 3 })).toHaveLength(2);
  });

  it('shows getting started information', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    expect(screen.getByText('User Management Dashboard')).toBeInTheDocument();
    expect(screen.getByText(/comprehensive platform for managing users/i)).toBeInTheDocument();
  });

  it('displays key features list', () => {
    render(
      <TestWrapper>
        <HomePage />
      </TestWrapper>
    );

    expect(screen.getByText('User Management')).toBeInTheDocument();
    expect(screen.getByText('Help & Documentation')).toBeInTheDocument();
    expect(screen.getByText(/complete crud operations/i)).toBeInTheDocument();
    expect(screen.getByText(/get help and learn about features/i)).toBeInTheDocument();
  });

  describe('Accessibility', () => {
    it('has accessible link text', () => {
      render(
        <TestWrapper>
          <HomePage />
        </TestWrapper>
      );

      const links = screen.getAllByRole('link');
      links.forEach(link => {
        expect(link).toHaveAccessibleName();
      });
    });

    it('uses proper heading structure', () => {
      render(
        <TestWrapper>
          <HomePage />
        </TestWrapper>
      );

      const h1 = screen.getByRole('heading', { level: 1 });
      const h3s = screen.getAllByRole('heading', { level: 3 });

      expect(h1).toBeInTheDocument();
      expect(h3s.length).toBeGreaterThan(0);
    });
  });
});
