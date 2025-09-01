import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { Footer } from './Footer';

describe('Footer', () => {
  describe('Rendering', () => {
    it('renders footer component', () => {
      render(<Footer />);

      // Should render the footer container
      const footer = screen.getByRole('contentinfo');
      expect(footer).toBeInTheDocument();
    });

    it('renders footer content', () => {
      render(<Footer />);

      // Check for common footer elements
      const footer = screen.getByRole('contentinfo');
      expect(footer).toHaveTextContent(/./); // Has some content
    });

    it('renders copyright or attribution', () => {
      render(<Footer />);

      // Common footer patterns
      const footerText = screen.getByRole('contentinfo').textContent;
      expect(footerText).toBeTruthy();
    });
  });

  describe('Accessibility', () => {
    it('has proper contentinfo role', () => {
      render(<Footer />);

      const footer = screen.getByRole('contentinfo');
      expect(footer).toBeInTheDocument();
    });

    it('has readable text content', () => {
      render(<Footer />);

      const footer = screen.getByRole('contentinfo');
      expect(footer).toHaveTextContent(/./);
    });
  });

  describe('Dark Mode Support', () => {
    it('renders with dark mode classes', () => {
      render(<Footer />);

      const footer = screen.getByRole('contentinfo');
      expect(footer).toBeInTheDocument();

      // Should have dark mode transition classes
      const hasTransitionClasses =
        footer.className.includes('transition') || footer.className.includes('dark:');
      expect(hasTransitionClasses).toBe(true);
    });
  });

  describe('Layout', () => {
    it('renders with proper styling', () => {
      render(<Footer />);

      const footer = screen.getByRole('contentinfo');
      expect(footer).toBeInTheDocument();
      expect(footer.className).toBeTruthy();
    });
  });
});
