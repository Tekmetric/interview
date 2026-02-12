import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import Header from './Header';

// Mock the SCSS import
jest.mock('./Header.scss', () => ({}));

// Wrapper component to provide router context
const renderWithRouter = (component) => {
  return render(
    <BrowserRouter>
      {component}
    </BrowserRouter>
  );
};

describe('Header', () => {
  const defaultProps = {
    title: 'Test Vehicle',
    subtitle: 'Test Subtitle'
  };

  describe('Rendering', () => {
    it('renders title correctly', () => {
      renderWithRouter(<Header {...defaultProps} />);
      expect(screen.getByText('Test Vehicle')).toBeInTheDocument();
    });

    it('renders subtitle correctly', () => {
      renderWithRouter(<Header {...defaultProps} />);
      expect(screen.getByText('Test Subtitle')).toBeInTheDocument();
    });

    it('renders without subtitle when not provided', () => {
      renderWithRouter(<Header title="Test Vehicle" />);
      expect(screen.getByText('Test Vehicle')).toBeInTheDocument();
      expect(screen.queryByText('Test Subtitle')).not.toBeInTheDocument();
    });
  });

  describe('Back Button', () => {
    it('shows back button when showBackButton is true', () => {
      renderWithRouter(<Header {...defaultProps} showBackButton={true} backUrl="/" />);
      expect(screen.getByRole('link', { name: /back/i })).toBeInTheDocument();
    });

    it('hides back button when showBackButton is false', () => {
      renderWithRouter(<Header {...defaultProps} showBackButton={false} />);
      expect(screen.queryByRole('link', { name: /back/i })).not.toBeInTheDocument();
    });

    it('hides back button when showBackButton is not specified', () => {
      renderWithRouter(<Header {...defaultProps} />);
      expect(screen.queryByRole('link', { name: /back/i })).not.toBeInTheDocument();
    });

    it('navigates to correct URL when back button is clicked', () => {
      renderWithRouter(<Header {...defaultProps} showBackButton={true} backUrl="/vehicles" />);
      
      const backButton = screen.getByRole('link', { name: /back/i });
      expect(backButton).toHaveAttribute('href', '/vehicles');
    });

    it('uses default back URL when backUrl is not specified', () => {
      renderWithRouter(<Header {...defaultProps} showBackButton={true} />);
      
      const backButton = screen.getByRole('link', { name: /back/i });
      expect(backButton).toHaveAttribute('href', '/');
    });
  });

  describe('Accessibility', () => {
    it('has proper heading structure', () => {
      renderWithRouter(<Header {...defaultProps} />);
      
      const title = screen.getByText('Test Vehicle');
      expect(title.tagName).toBe('H1');
    });

    it('provides accessible back button text', () => {
      renderWithRouter(<Header {...defaultProps} showBackButton={true} />);
      
      const backButton = screen.getByRole('link', { name: /back/i });
      expect(backButton).toBeInTheDocument();
    });
  });

  describe('Edge Cases', () => {
    it('handles empty title gracefully', () => {
      renderWithRouter(<Header title="" subtitle="Test Subtitle" />);
      expect(screen.getByText('Test Subtitle')).toBeInTheDocument();
    });

    it('handles very long titles', () => {
      const longTitle = 'This is a very long vehicle title that might exceed normal header widths and should be handled gracefully by the CSS styling';
      renderWithRouter(<Header title={longTitle} subtitle="Test Subtitle" />);
      expect(screen.getByText(longTitle)).toBeInTheDocument();
    });

    it('handles very long subtitles', () => {
      const longSubtitle = 'This is a very long subtitle that contains a lot of information about the vehicle including VIN numbers, license plates, and other details that might exceed normal header widths';
      renderWithRouter(<Header title="Test Vehicle" subtitle={longSubtitle} />);
      expect(screen.getByText(longSubtitle)).toBeInTheDocument();
    });
  });

  describe('Styling Classes', () => {
    it('applies correct CSS classes', () => {
      const { container } = renderWithRouter(<Header {...defaultProps} />);
      
      const headerElement = container.querySelector('header');
      expect(headerElement).toHaveClass('header');
    });
  });
});
