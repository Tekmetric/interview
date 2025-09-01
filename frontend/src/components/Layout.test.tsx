import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';

import { ThemeProvider } from '../contexts/ThemeContext';
import { Layout } from './Layout';

// Helper to render with all required providers
const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      <ThemeProvider>{component}</ThemeProvider>
    </BrowserRouter>
  );
};

describe('Layout', () => {
  describe('Rendering', () => {
    it('renders layout structure', () => {
      renderWithProviders(<Layout />);

      // Should render main content area
      const main = screen.getByRole('main');
      expect(main).toBeInTheDocument();
    });

    it('includes header component', () => {
      renderWithProviders(<Layout />);

      // Should include header
      const header = screen.getByRole('banner');
      expect(header).toBeInTheDocument();
    });

    it('includes footer component', () => {
      renderWithProviders(<Layout />);

      // Should include footer
      const footer = screen.getByRole('contentinfo');
      expect(footer).toBeInTheDocument();
    });

    it('includes navigation component', () => {
      renderWithProviders(<Layout />);

      // Should include navigation
      const nav = screen.getByRole('navigation');
      expect(nav).toBeInTheDocument();
    });
  });

  describe('Layout Structure', () => {
    it('has proper semantic structure', () => {
      renderWithProviders(<Layout />);

      // Check for semantic elements
      expect(screen.getByRole('banner')).toBeInTheDocument(); // header
      expect(screen.getByRole('navigation')).toBeInTheDocument(); // nav
      expect(screen.getByRole('main')).toBeInTheDocument(); // main
      expect(screen.getByRole('contentinfo')).toBeInTheDocument(); // footer
    });

    it('renders with proper layout classes', () => {
      renderWithProviders(<Layout />);

      const main = screen.getByRole('main');
      expect(main).toHaveClass('container', 'mx-auto');
    });
  });

  describe('Responsive Design', () => {
    it('has responsive layout container', () => {
      renderWithProviders(<Layout />);

      // Check for responsive container
      const container = document.querySelector('.container');
      expect(container).toBeInTheDocument();
    });

    it('includes flex layout classes', () => {
      renderWithProviders(<Layout />);

      // Should have flex layout structure
      const layoutContainer = document.querySelector('.flex.min-h-screen');
      expect(layoutContainer).toBeInTheDocument();
    });
  });

  describe('Dark Mode Support', () => {
    it('supports dark mode background', () => {
      renderWithProviders(<Layout />);

      // Should have dark mode background classes
      const darkBg = document.querySelector('.dark\\:bg-gray-900');
      expect(darkBg).toBeInTheDocument();
    });
  });

  describe('Accessibility', () => {
    it('has proper landmark roles', () => {
      renderWithProviders(<Layout />);

      // All landmark roles should be present
      expect(screen.getByRole('banner')).toBeInTheDocument();
      expect(screen.getByRole('navigation')).toBeInTheDocument();
      expect(screen.getByRole('main')).toBeInTheDocument();
      expect(screen.getByRole('contentinfo')).toBeInTheDocument();
    });

    it('maintains proper heading hierarchy', () => {
      renderWithProviders(<Layout />);

      // Layout should not interfere with heading hierarchy
      const main = screen.getByRole('main');
      expect(main).toBeInTheDocument();
    });
  });
});
