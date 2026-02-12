import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import AnnotationOverlay from './AnnotationOverlay';

// Mock the SCSS import
jest.mock('./AnnotationOverlay.scss', () => ({}));

describe('AnnotationOverlay', () => {
  const defaultProps = {
    imageSrc: 'test-image.jpg',
    annotations: [],
    onChange: jest.fn(),
    isEditMode: true
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Rendering', () => {
    it('renders the image correctly', () => {
      render(<AnnotationOverlay {...defaultProps} />);
      const image = screen.getByAltText('Vehicle');
      expect(image).toBeInTheDocument();
      expect(image).toHaveAttribute('src', 'test-image.jpg');
    });

    it('renders existing annotations', () => {
      const annotations = [
        {
          id: 1,
          x: 0.1,
          y: 0.2,
          width: 0.1,
          height: 0.1,
          label: 'Test Annotation',
          category: 'red'
        }
      ];
      
      render(<AnnotationOverlay {...defaultProps} annotations={annotations} />);
      expect(screen.getByText('Test Annotation')).toBeInTheDocument();
    });

    it('filters out invalid annotations', () => {
      const invalidAnnotations = [
        { id: 1, x: null, y: 0.2, width: 0.1, height: 0.1, label: 'Invalid', category: 'red' },
        { id: 2, x: 0.1, y: 0.2, width: 0.1, height: 0.1, label: 'Valid', category: 'red' }
      ];
      
      render(<AnnotationOverlay {...defaultProps} annotations={invalidAnnotations} />);
      expect(screen.queryByText('Invalid')).not.toBeInTheDocument();
      expect(screen.getByText('Valid')).toBeInTheDocument();
    });
  });

  describe('Edit Mode', () => {
    const annotation = {
      id: 1,
      x: 0.1,
      y: 0.2,
      width: 0.1,
      height: 0.1,
      label: 'Test Annotation',
      category: 'red'
    };

    it('shows edit controls when in edit mode', () => {
      render(<AnnotationOverlay {...defaultProps} annotations={[annotation]} isEditMode={true} />);
      expect(screen.getByRole('button', { name: '×' })).toBeInTheDocument();
    });

    it('hides edit controls when not in edit mode', () => {
      render(<AnnotationOverlay {...defaultProps} annotations={[annotation]} isEditMode={false} />);
      expect(screen.queryByRole('button', { name: '×' })).not.toBeInTheDocument();
    });
  });

  describe('Annotation Creation', () => {
    beforeEach(() => {
      // Mock getBoundingClientRect for consistent coordinate calculation
      const mockRect = { left: 0, top: 0, width: 200, height: 200 };
      jest.spyOn(Element.prototype, 'getBoundingClientRect').mockReturnValue(mockRect);
    });

    afterEach(() => {
      jest.restoreAllMocks();
    });

    it('creates new annotation on image click', async () => {
      const onChange = jest.fn();
      render(<AnnotationOverlay {...defaultProps} onChange={onChange} />);
      
      const imageOverlay = screen.getByTestId('image-overlay');
      fireEvent.mouseDown(imageOverlay, { clientX: 100, clientY: 100 });
      
      await waitFor(() => {
        expect(onChange).toHaveBeenCalledWith(expect.arrayContaining([
          expect.objectContaining({
            label: 'Annotation 1',
            category: 'red'
          })
        ]));
      });
    });

    it('creates annotation with correct normalized coordinates', async () => {
      const onChange = jest.fn();
      render(<AnnotationOverlay {...defaultProps} onChange={onChange} />);
      
      const imageOverlay = screen.getByTestId('image-overlay');
      fireEvent.mouseDown(imageOverlay, { clientX: 100, clientY: 100 });
      
      await waitFor(() => {
        const call = onChange.mock.calls[0][0];
        const newAnnotation = call[0];
        expect(newAnnotation.x).toBeGreaterThanOrEqual(0);
        expect(newAnnotation.y).toBeGreaterThanOrEqual(0);
        expect(newAnnotation.width).toBeGreaterThan(0);
        expect(newAnnotation.height).toBeGreaterThan(0);
      });
    });
  });

  describe('Annotation Editing', () => {
    const annotation = {
      id: 1,
      x: 0.1,
      y: 0.2,
      width: 0.1,
      height: 0.1,
      label: 'Test Annotation',
      category: 'red'
    };

    it('enters edit mode when label is clicked', () => {
      render(<AnnotationOverlay {...defaultProps} annotations={[annotation]} />);
      
      const label = screen.getByText('Test Annotation');
      fireEvent.click(label);
      
      expect(screen.getByDisplayValue('Test Annotation')).toBeInTheDocument();
    });

    it('saves label changes on blur', async () => {
      const onChange = jest.fn();
      render(<AnnotationOverlay {...defaultProps} annotations={[annotation]} onChange={onChange} />);
      
      const label = screen.getByText('Test Annotation');
      fireEvent.click(label);
      
      const input = screen.getByDisplayValue('Test Annotation');
      fireEvent.change(input, { target: { value: 'Updated Label' } });
      fireEvent.blur(input);
      
      await waitFor(() => {
        expect(onChange).toHaveBeenCalledWith([
          expect.objectContaining({ label: 'Updated Label' })
        ]);
      });
    });

    it('saves label changes on Enter key', async () => {
      const onChange = jest.fn();
      render(<AnnotationOverlay {...defaultProps} annotations={[annotation]} onChange={onChange} />);
      
      const label = screen.getByText('Test Annotation');
      fireEvent.click(label);
      
      const input = screen.getByDisplayValue('Test Annotation');
      fireEvent.change(input, { target: { value: 'Updated Label' } });
      fireEvent.keyDown(input, { key: 'Enter' });
      
      await waitFor(() => {
        expect(onChange).toHaveBeenCalledWith([
          expect.objectContaining({ label: 'Updated Label' })
        ]);
      });
    });
  });

  describe('Category Management', () => {
    const annotation = {
      id: 1,
      x: 0.1,
      y: 0.2,
      width: 0.1,
      height: 0.1,
      label: 'Test Annotation',
      category: 'red'
    };

    it('displays category selector buttons', () => {
      render(<AnnotationOverlay {...defaultProps} annotations={[annotation]} />);
      
      expect(screen.getByTitle('red')).toBeInTheDocument();
      expect(screen.getByTitle('yellow')).toBeInTheDocument();
      expect(screen.getByTitle('green')).toBeInTheDocument();
    });

    it('changes annotation category when category button is clicked', async () => {
      const onChange = jest.fn();
      render(<AnnotationOverlay {...defaultProps} annotations={[annotation]} onChange={onChange} />);
      
      const yellowButton = screen.getByTitle('yellow');
      fireEvent.click(yellowButton);
      
      await waitFor(() => {
        expect(onChange).toHaveBeenCalledWith([
          expect.objectContaining({ category: 'yellow' })
        ]);
      });
    });
  });

  describe('Annotation Deletion', () => {
    const annotation = {
      id: 1,
      x: 0.1,
      y: 0.2,
      width: 0.1,
      height: 0.1,
      label: 'Test Annotation',
      category: 'red'
    };

    it('deletes annotation when delete button is clicked', async () => {
      const onChange = jest.fn();
      render(<AnnotationOverlay {...defaultProps} annotations={[annotation]} onChange={onChange} />);
      
      const deleteButton = screen.getByRole('button', { name: '×' });
      fireEvent.click(deleteButton);
      
      await waitFor(() => {
        expect(onChange).toHaveBeenCalledWith([]);
      });
    });
  });

  describe('Touch Events', () => {
    it('prevents default touch behavior', () => {
      render(<AnnotationOverlay {...defaultProps} />);
      
      const imageOverlay = screen.getByTestId('image-overlay');
      const touchEvent = new TouchEvent('touchstart', { bubbles: true });
      const preventDefaultSpy = jest.spyOn(touchEvent, 'preventDefault');
      
      fireEvent(imageOverlay, touchEvent);
      
      expect(preventDefaultSpy).toHaveBeenCalled();
    });
  });

  describe('Error Handling', () => {
    it('handles invalid bounds gracefully', () => {
      const onChange = jest.fn();
      render(<AnnotationOverlay {...defaultProps} onChange={onChange} />);
      
      // Mock getBoundingClientRect to return invalid bounds
      const mockRect = { left: 0, top: 0, width: 0, height: 0 };
      jest.spyOn(Element.prototype, 'getBoundingClientRect').mockReturnValue(mockRect);
      
      const imageOverlay = screen.getByTestId('image-overlay');
      fireEvent.mouseDown(imageOverlay, { clientX: 100, clientY: 100 });
      
      // Should not call onChange with invalid bounds
      expect(onChange).not.toHaveBeenCalled();
    });
  });
});
