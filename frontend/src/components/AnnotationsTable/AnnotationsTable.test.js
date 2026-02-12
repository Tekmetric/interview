import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import AnnotationsTable from './AnnotationsTable';

// Mock the SCSS import
jest.mock('./AnnotationsTable.scss', () => ({}));

describe('AnnotationsTable', () => {
  const mockAnnotations = [
    {
      id: 1,
      label: 'Windshield Crack',
      category: 'red'
    },
    {
      id: 2,
      label: 'Dent on Hood',
      category: 'yellow'
    },
    {
      id: 3,
      label: 'New Tires',
      category: 'green'
    }
  ];

  describe('Rendering', () => {
    it('renders table headers correctly', () => {
      render(<AnnotationsTable annotations={mockAnnotations} imageNumber={1} />);
      
      expect(screen.getByText('#')).toBeInTheDocument();
      expect(screen.getByText('Label')).toBeInTheDocument();
      expect(screen.getByText('Category')).toBeInTheDocument();
    });

    it('renders all annotations with correct data', () => {
      render(<AnnotationsTable annotations={mockAnnotations} imageNumber={1} />);
      
      expect(screen.getByText('Windshield Crack')).toBeInTheDocument();
      expect(screen.getByText('Dent on Hood')).toBeInTheDocument();
      expect(screen.getByText('New Tires')).toBeInTheDocument();
      
      expect(screen.getByText('red')).toBeInTheDocument();
      expect(screen.getByText('yellow')).toBeInTheDocument();
      expect(screen.getByText('green')).toBeInTheDocument();
    });

    it('displays correct row numbers', () => {
      render(<AnnotationsTable annotations={mockAnnotations} imageNumber={1} />);
      
      expect(screen.getByText('1')).toBeInTheDocument();
      expect(screen.getByText('2')).toBeInTheDocument();
      expect(screen.getByText('3')).toBeInTheDocument();
    });
  });

  describe('Header Display', () => {
    it('shows header with image number and count when showHeader is true', () => {
      render(<AnnotationsTable annotations={mockAnnotations} imageNumber={1} showHeader={true} />);
      
      expect(screen.getByText('Image 1 Annotations (3)')).toBeInTheDocument();
    });

    it('hides header when showHeader is false', () => {
      render(<AnnotationsTable annotations={mockAnnotations} imageNumber={1} showHeader={false} />);
      
      expect(screen.queryByText(/Image \d+ Annotations/)).not.toBeInTheDocument();
    });

    it('defaults to showing header when showHeader is not specified', () => {
      render(<AnnotationsTable annotations={mockAnnotations} imageNumber={1} />);
      
      expect(screen.getByText('Image 1 Annotations (3)')).toBeInTheDocument();
    });
  });

  describe('Empty State', () => {
    it('renders nothing when annotations array is empty', () => {
      const { container } = render(<AnnotationsTable annotations={[]} imageNumber={1} />);
      
      expect(container.firstChild).toBeNull();
    });

    it('renders nothing when annotations is null', () => {
      const { container } = render(<AnnotationsTable annotations={null} imageNumber={1} />);
      
      expect(container.firstChild).toBeNull();
    });

    it('renders nothing when annotations is undefined', () => {
      const { container } = render(<AnnotationsTable annotations={undefined} imageNumber={1} />);
      
      expect(container.firstChild).toBeNull();
    });

    it('renders nothing when annotations is not an array', () => {
      const { container } = render(<AnnotationsTable annotations="not an array" imageNumber={1} />);
      
      expect(container.firstChild).toBeNull();
    });
  });

  describe('Category Styling', () => {
    it('applies correct CSS classes for each category', () => {
      render(<AnnotationsTable annotations={mockAnnotations} imageNumber={1} />);
      
      const redCategory = screen.getByText('red');
      const yellowCategory = screen.getByText('yellow');
      const greenCategory = screen.getByText('green');
      
      expect(redCategory).toHaveClass('category-red');
      expect(yellowCategory).toHaveClass('category-yellow');
      expect(greenCategory).toHaveClass('category-green');
    });
  });

  describe('Edge Cases', () => {
    it('handles annotations with missing properties gracefully', () => {
      const incompleteAnnotations = [
        { id: 1, label: 'Complete', category: 'red' },
        { id: 2, label: 'Missing Category' },
        { id: 3, category: 'green' }
      ];
      
      render(<AnnotationsTable annotations={incompleteAnnotations} imageNumber={1} />);
      
      expect(screen.getByText('Complete')).toBeInTheDocument();
      expect(screen.getByText('Missing Category')).toBeInTheDocument();
      expect(screen.getByText('green')).toBeInTheDocument();
    });

    it('handles very long labels', () => {
      const longLabel = 'This is a very long annotation label that might exceed normal table cell widths and should be handled gracefully by the CSS styling';
      const longAnnotations = [{ id: 1, label: longLabel, category: 'red' }];
      
      render(<AnnotationsTable annotations={longAnnotations} imageNumber={1} />);
      
      expect(screen.getByText(longLabel)).toBeInTheDocument();
    });
  });
});
