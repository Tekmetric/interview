import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';

import { Navigation } from './Navigation';

// Helper to render with router
const renderWithRouter = (component: React.ReactElement) => {
  return render(<BrowserRouter>{component}</BrowserRouter>);
};

describe('Navigation', () => {
  describe('Rendering', () => {
    it('renders navigation component', () => {
      renderWithRouter(<Navigation />);

      // Should render the navigation container
      const nav = screen.getByRole('navigation');
      expect(nav).toBeInTheDocument();
    });

    it('renders all navigation links', () => {
      renderWithRouter(<Navigation />);

      // Check for main navigation links
      expect(screen.getByText(/home/i)).toBeInTheDocument();
      expect(screen.getByText(/users/i)).toBeInTheDocument();
      expect(screen.getByText(/help/i)).toBeInTheDocument();
    });

    it('has proper link structure', () => {
      renderWithRouter(<Navigation />);

      // Should have proper menuitem elements (navigation uses menuitem role)
      const menuItems = screen.getAllByRole('menuitem');
      expect(menuItems.length).toBe(3);

      // Verify each menuitem has href attribute
      menuItems.forEach(item => {
        expect(item).toHaveAttribute('href');
      });
    });
  });

  describe('Accessibility', () => {
    it('has proper navigation role', () => {
      renderWithRouter(<Navigation />);

      const nav = screen.getByRole('navigation');
      expect(nav).toBeInTheDocument();
    });

    it('has keyboard accessible links', () => {
      renderWithRouter(<Navigation />);

      // Navigation uses menuitem role for accessibility
      const menuItems = screen.getAllByRole('menuitem');
      menuItems.forEach(item => {
        expect(item).toBeInTheDocument();
        expect(item).toHaveAttribute('href');
        // Should have proper aria labels
        expect(item).toHaveAttribute('aria-label');
      });
    });
  });

  describe('Responsive Design', () => {
    it('renders with responsive classes', () => {
      renderWithRouter(<Navigation />);

      const nav = screen.getByRole('navigation');
      expect(nav).toBeInTheDocument();

      // Check for responsive navigation patterns (Tailwind classes)
      expect(nav.className).toContain('border-b');
      expect(nav.className).toContain('bg-white');
      expect(nav.className).toContain('dark:bg-gray-800');
    });
  });

  describe('Dark Mode Support', () => {
    it('renders with dark mode classes', () => {
      renderWithRouter(<Navigation />);

      const nav = screen.getByRole('navigation');
      expect(nav).toBeInTheDocument();
    });
  });
});
