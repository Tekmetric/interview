import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import '@testing-library/jest-dom';
import VehiclePage from './VehiclePage';

// Mock the components
jest.mock('../components', () => ({
  Header: ({ title, subtitle, showBackButton, backUrl }) => (
    <header data-testid="header">
      <h1>{title}</h1>
      <p>{subtitle}</p>
      {showBackButton && <a href={backUrl}>Back</a>}
    </header>
  ),
  AnnotationsTable: ({ annotations, imageNumber }) => (
    <div data-testid={`annotations-table-${imageNumber}`}>
      <h4>Image {imageNumber} Annotations ({annotations.length})</h4>
      <table>
        <tbody>
          {annotations.map((ann, index) => (
            <tr key={ann.id}>
              <td>{index + 1}</td>
              <td>{ann.label}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}));

// Mock AnnotationOverlay separately
jest.mock('../components/AnnotationOverlay', () => {
  return function MockAnnotationOverlay({ imageSrc, annotations, onChange, isEditMode }) {
    return (
      <div data-testid="annotation-overlay" data-edit-mode={isEditMode}>
        <img src={imageSrc} alt="Vehicle" />
      </div>
    );
  };
});

// Mock the config
jest.mock('../config', () => ({
  API_BASE_URL: 'http://localhost/tekmetric-api/'
}));

// Mock fetch globally
global.fetch = jest.fn();

// Wrapper component to provide router context
const renderWithRouter = (component) => {
  return render(
    <BrowserRouter>
      {component}
    </BrowserRouter>
  );
};

describe('VehiclePage', () => {
  const mockVehicle = {
    id: 1,
    make: 'Toyota',
    model: 'Camry',
    year: 2020,
    vin: '1HGBH41JXMN109186',
    license_plate: 'ABC123',
    images: [
      { id: 1, fileName: 'image1.jpg' },
      { id: 2, fileName: 'image2.jpg' }
    ]
  };

  const mockAnnotations = [
    { id: 1, label: 'Test Annotation 1', category: 'red' },
    { id: 2, label: 'Test Annotation 2', category: 'yellow' }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Vehicle Loading', () => {
    it('loads and displays vehicle information', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Toyota Camry (2020)')).toBeInTheDocument();
        expect(screen.getByText('VIN: 1HGBH41JXMN109186 | License: ABC123')).toBeInTheDocument();
      });
    });

    it('shows loading state initially', () => {
      renderWithRouter(<VehiclePage />);
      expect(screen.getByText('Loading vehicle data...')).toBeInTheDocument();
    });

    it('handles API errors gracefully', async () => {
      global.fetch.mockRejectedValueOnce(new Error('API Error'));
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Error: API Error')).toBeInTheDocument();
      });
    });
  });

  describe('Image Display', () => {
    it('renders all vehicle images', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        const images = screen.getAllByAltText('Vehicle');
        expect(images).toHaveLength(2);
        expect(images[0]).toHaveAttribute('src', 'image1.jpg');
        expect(images[1]).toHaveAttribute('src', 'image2.jpg');
      });
    });

    it('shows no images message when vehicle has no images', async () => {
      const vehicleWithoutImages = { ...mockVehicle, images: [] };
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => vehicleWithoutImages
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('No images available for this vehicle.')).toBeInTheDocument();
      });
    });
  });

  describe('Edit Mode', () => {
    it('toggles edit mode when button is clicked', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Edit Mode')).toBeInTheDocument();
      });
      
      const editButton = screen.getByText('Edit Mode');
      fireEvent.click(editButton);
      
      expect(screen.getByText('View Mode')).toBeInTheDocument();
    });

    it('shows clear all annotations button only in edit mode', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Edit Mode')).toBeInTheDocument();
      });
      
      // Initially in view mode, should not show clear button
      expect(screen.queryByText('Clear All Annotations')).not.toBeInTheDocument();
      
      // Switch to edit mode
      const editButton = screen.getByText('Edit Mode');
      fireEvent.click(editButton);
      
      // Should show clear button in edit mode
      expect(screen.getByText('Clear All Annotations')).toBeInTheDocument();
    });
  });

  describe('File Upload', () => {
    it('handles file selection', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Upload New Image')).toBeInTheDocument();
      });
      
      const fileInput = screen.getByRole('button', { name: /upload/i }).previousElementSibling;
      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
      
      fireEvent.change(fileInput, { target: { files: [file] } });
      
      expect(screen.getByRole('button', { name: /upload/i })).not.toBeDisabled();
    });

    it('handles upload process', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Upload New Image')).toBeInTheDocument();
      });
      
      const fileInput = screen.getByRole('button', { name: /upload/i }).previousElementSibling;
      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
      
      fireEvent.change(fileInput, { target: { files: [file] } });
      
      const uploadButton = screen.getByRole('button', { name: /upload/i });
      fireEvent.click(uploadButton);
      
      await waitFor(() => {
        expect(screen.getByText('Uploading...')).toBeInTheDocument();
      });
    });
  });

  describe('Annotation Management', () => {
    it('displays annotations table for each image', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByTestId('annotations-table-1')).toBeInTheDocument();
        expect(screen.getByTestId('annotations-table-2')).toBeInTheDocument();
      });
    });

    it('displays correct annotation counts in tables', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Image 1 Annotations (2)')).toBeInTheDocument();
        expect(screen.getByText('Image 2 Annotations (2)')).toBeInTheDocument();
      });
    });

    it('displays annotation overlay for each image', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        const overlays = screen.getAllByTestId('annotation-overlay');
        expect(overlays).toHaveLength(2);
      });
    });
  });

  describe('Error Handling', () => {
    it('handles network errors gracefully', async () => {
      global.fetch.mockRejectedValueOnce(new Error('Network Error'));
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Error: Network Error')).toBeInTheDocument();
      });
    });

    it('handles malformed API responses', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ invalid: 'response' })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('No images available for this vehicle.')).toBeInTheDocument();
      });
    });
  });

  describe('Accessibility', () => {
    it('provides proper form labels', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        expect(screen.getByText('Upload New Image')).toBeInTheDocument();
      });
      
      const fileInput = screen.getByRole('button', { name: /upload/i }).previousElementSibling;
      expect(fileInput).toHaveAttribute('type', 'file');
      expect(fileInput).toHaveAttribute('accept', 'image/*');
    });

    it('has proper button states', async () => {
      // Mock successful vehicle fetch
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockVehicle
      });
      // Mock successful annotations fetch for each image
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ success: true, annotations: mockAnnotations })
      });
      
      renderWithRouter(<VehiclePage />);
      
      await waitFor(() => {
        const uploadButton = screen.getByRole('button', { name: /upload/i });
        expect(uploadButton).toBeDisabled(); // No file selected initially
      });
    });
  });
});
