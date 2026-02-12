import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import '@testing-library/jest-dom';
import App from './App';

// Mock the components
jest.mock('./components', () => ({
  Header: ({ title, subtitle }) => (
    <header data-testid="header">
      <h1>{title}</h1>
      <p>{subtitle}</p>
    </header>
  )
}));

// Mock the config
jest.mock('./config', () => ({
  API_BASE_URL: 'http://localhost/tekmetric-api/'
}));

// Mock fetch globally
global.fetch = jest.fn();

// Since App.js now includes BrowserRouter, we don't need to wrap it again
const renderApp = () => {
  return render(<App />);
};

describe('App', () => {
  const mockVehicles = [
    {
      id: 1,
      make: 'Toyota',
      model: 'Camry',
      year: 2020,
      vin: '1HGBH41JXMN109186',
      license_plate: 'ABC123',
      images: ['image1.jpg', 'image2.jpg']
    },
    {
      id: 2,
      make: 'Honda',
      model: 'Civic',
      year: 2019,
      vin: '2HGBH41JXMN109187',
      license_plate: 'XYZ789',
      images: ['image3.jpg']
    }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    // Mock successful vehicles fetch
    global.fetch.mockResolvedValue({
      ok: true,
      json: async () => mockVehicles
    });
  });

  describe('Home Page (Vehicle List)', () => {
    it('renders vehicle list on home page', async () => {
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByText('Toyota')).toBeInTheDocument();
        expect(screen.getByText('Camry')).toBeInTheDocument();
        expect(screen.getByText('Honda')).toBeInTheDocument();
        expect(screen.getByText('Civic')).toBeInTheDocument();
      });
    });

    it('displays vehicle information correctly', async () => {
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByText('1HGBH41JXMN109186')).toBeInTheDocument();
        expect(screen.getByText('ABC123')).toBeInTheDocument();
        expect(screen.getByText('2HGBH41JXMN109187')).toBeInTheDocument();
        expect(screen.getByText('XYZ789')).toBeInTheDocument();
      });
    });

    it('shows loading state initially', () => {
      renderApp();
      expect(screen.getByText('Loading vehicles...')).toBeInTheDocument();
    });

    it('handles API errors gracefully', async () => {
      global.fetch.mockRejectedValueOnce(new Error('API Error'));
      
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByText('Error: API Error')).toBeInTheDocument();
      });
    });

    it('displays vehicle images when vehicle is selected', async () => {
      renderApp();
      
      await waitFor(() => {
        expect(screen.getAllByText('Quick look')).toHaveLength(2);
      });
      
      // Click on first vehicle's Quick look button
      const selectButton = screen.getAllByText('Quick look')[0];
      fireEvent.click(selectButton);
      
      await waitFor(() => {
        const images = screen.getAllByAltText(/Vehicle \d+/);
        expect(images).toHaveLength(2); // First vehicle has 2 images
      });
    });

    it('handles vehicles without images', async () => {
      const vehiclesWithoutImages = mockVehicles.map(v => ({ ...v, images: [] }));
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => vehiclesWithoutImages
      });
      
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByText('Toyota')).toBeInTheDocument();
        expect(screen.getByText('Honda')).toBeInTheDocument();
      });
    });
  });

  describe('Header', () => {
    it('displays main header on home page', async () => {
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByTestId('header')).toBeInTheDocument();
        expect(screen.getByText('Vehicle List')).toBeInTheDocument();
        expect(screen.getByText('Select a vehicle to view and annotate images')).toBeInTheDocument();
      });
    });
  });

  describe('Error Handling', () => {
    it('handles network errors gracefully', async () => {
      global.fetch.mockRejectedValueOnce(new Error('Network Error'));
      
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByText('Error: Network Error')).toBeInTheDocument();
      });
    });

    it('handles malformed API responses gracefully', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ invalid: 'response' })
      });
      
      renderApp();
      
      await waitFor(() => {
        // Should not crash and should show loading state
        expect(screen.getByText('Loading vehicles...')).toBeInTheDocument();
      });
    });

    it('handles empty vehicle list', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => []
      });
      
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByText('No vehicles found.')).toBeInTheDocument();
      });
    });
  });

  describe('Accessibility', () => {
    it('provides proper heading structure', async () => {
      renderApp();
      
      await waitFor(() => {
        const header = screen.getByTestId('header');
        const h1 = header.querySelector('h1');
        expect(h1).toHaveTextContent('Vehicle List');
      });
    });

    it('has proper button attributes', async () => {
      renderApp();
      
      await waitFor(() => {
        const selectButtons = screen.getAllByText('Quick look');
        selectButtons.forEach(button => {
          expect(button.tagName).toBe('BUTTON');
        });
      });
    });
  });

  describe('Responsive Design', () => {
    it('handles different screen sizes gracefully', async () => {
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByText('Toyota')).toBeInTheDocument();
      });
      
      // Test that the layout doesn't break with different content lengths
      const longVehicleName = 'Very Long Vehicle Name That Might Exceed Normal Display Widths';
      const vehiclesWithLongNames = mockVehicles.map((v, index) => ({
        ...v,
        make: index === 0 ? longVehicleName : v.make
      }));
      
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => vehiclesWithLongNames
      });
      
      // Re-render with new data
      renderApp();
      
      await waitFor(() => {
        expect(screen.getByText(longVehicleName)).toBeInTheDocument();
      });
    });
  });
});
