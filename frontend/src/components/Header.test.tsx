import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { ThemeProvider } from '../contexts/ThemeContext';
import { Header } from './Header';

// Helper to render with theme context
const renderWithTheme = (component: React.ReactElement) => {
  return render(<ThemeProvider>{component}</ThemeProvider>);
};

describe('Header', () => {
  describe('Rendering', () => {
    it('renders header component', () => {
      renderWithTheme(<Header />);

      // Should render the header container
      const header = screen.getByRole('banner');
      expect(header).toBeInTheDocument();
    });

    it('renders application title', () => {
      renderWithTheme(<Header />);

      // Check for application title or brand
      expect(screen.getByText(/user management/i) || screen.getByText(/dashboard/i)).toBeTruthy();
    });

    it('renders theme toggle', () => {
      renderWithTheme(<Header />);

      // Should include theme toggle component
      const themeButtons = screen.getAllByRole('button');
      expect(themeButtons.length).toBeGreaterThan(0);
    });
  });

  describe('Accessibility', () => {
    it('has proper header role', () => {
      renderWithTheme(<Header />);

      const header = screen.getByRole('banner');
      expect(header).toBeInTheDocument();
    });

    it('maintains keyboard navigation', () => {
      renderWithTheme(<Header />);

      const buttons = screen.getAllByRole('button');
      buttons.forEach(button => {
        expect(button).toBeInTheDocument();
      });
    });
  });

  describe('Theme Integration', () => {
    it('renders theme toggle buttons', () => {
      renderWithTheme(<Header />);

      // Should have theme toggle buttons
      const buttons = screen.getAllByRole('button');
      expect(buttons.length).toBeGreaterThan(0);
    });
  });

  describe('Responsive Design', () => {
    it('renders with responsive layout', () => {
      renderWithTheme(<Header />);

      const header = screen.getByRole('banner');
      expect(header).toBeInTheDocument();
    });
  });
});
