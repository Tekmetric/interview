import React from 'react';
import { render, fireEvent, wait } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import App from './App';
import './i18n';
import { ThemeProvider } from './contexts/ThemeContext';

// Mock BarChart
jest.mock('./components/BarChart', () => {
  return function BarChart() {
    return <div data-testid="bar-chart">BarChart</div>;
  };
});

const renderApp = () => {
  return render(
    <ThemeProvider>
      <App />
    </ThemeProvider>
  );
};

// Mock Pokemon data
const mockPokemonData = [
  {
    id: 1,
    name: 'bulbasaur',
    height: 7,
    weight: 69,
    sprites: { front_default: 'https://example.com/1.png' },
    types: [{ type: { name: 'grass' } }, { type: { name: 'poison' } }],
    stats: [
      { stat: { name: 'hp' }, base_stat: 45 },
      { stat: { name: 'attack' }, base_stat: 49 },
      { stat: { name: 'defense' }, base_stat: 49 },
      { stat: { name: 'special-attack' }, base_stat: 65 },
      { stat: { name: 'special-defense' }, base_stat: 65 },
      { stat: { name: 'speed' }, base_stat: 45 }
    ]
  },
  {
    id: 25,
    name: 'pikachu',
    height: 4,
    weight: 60,
    sprites: { front_default: 'https://example.com/25.png' },
    types: [{ type: { name: 'electric' } }],
    stats: [
      { stat: { name: 'hp' }, base_stat: 35 },
      { stat: { name: 'attack' }, base_stat: 55 },
      { stat: { name: 'defense' }, base_stat: 40 },
      { stat: { name: 'special-attack' }, base_stat: 50 },
      { stat: { name: 'special-defense' }, base_stat: 50 },
      { stat: { name: 'speed' }, base_stat: 90 }
    ]
  },
];

describe('App Integration Tests', () => {
  beforeEach(() => {
    global.fetch = jest.fn((url) => {
      const id = parseInt(url.split('/').pop());
      const pokemon = mockPokemonData.find(p => p.id === id);

      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve(pokemon || null)
      });
    });
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('loads and displays Pokemon data', async () => {
    const { getByText } = renderApp();

    await wait(() => {
      expect(getByText(/Bulbasaur/i)).toBeInTheDocument();
    }, { timeout: 5000 });

    expect(getByText(/Pikachu/i)).toBeInTheDocument();
  });

  test('search functionality filters Pokemon', async () => {
    const { getByPlaceholderText, getByText, queryByText } = renderApp();

    await wait(() => {
      expect(getByText(/Bulbasaur/i)).toBeInTheDocument();
    }, { timeout: 5000 });

    const searchInput = getByPlaceholderText(/Search by name, number, or type/i);

    // Search for Pikachu
    fireEvent.change(searchInput, { target: { value: 'pikachu' } });

    expect(getByText(/Pikachu/i)).toBeInTheDocument();
    expect(queryByText(/Bulbasaur/i)).not.toBeInTheDocument();
  });

  test('search by type filters correctly', async () => {
    const { getByPlaceholderText, getByText, queryByText } = renderApp();

    await wait(() => {
      expect(getByText(/Bulbasaur/i)).toBeInTheDocument();
    }, { timeout: 5000 });

    const searchInput = getByPlaceholderText(/Search by name, number, or type/i);

    // Search for electric type
    fireEvent.change(searchInput, { target: { value: 'electric' } });

    expect(getByText(/Pikachu/i)).toBeInTheDocument();
    expect(queryByText(/Bulbasaur/i)).not.toBeInTheDocument();
  });

  test('dark mode toggle works', async () => {
    const { getByRole } = renderApp();

    await wait(() => {
      expect(getByRole('heading', { name: /Pokédex/i })).toBeInTheDocument();
    }, { timeout: 5000 });

    // Find and click dark mode toggle
    const darkModeButton = getByRole('button', { name: /Dark/i });
    fireEvent.click(darkModeButton);

    // Check if dark class was added
    expect(document.documentElement.classList.contains('dark')).toBe(true);

    // Click again to toggle back
    const lightModeButton = getByRole('button', { name: /Light/i });
    fireEvent.click(lightModeButton);

    expect(document.documentElement.classList.contains('dark')).toBe(false);
  });

  test('language switcher works', async () => {
    const { getByRole, getByText } = renderApp();

    await wait(() => {
      expect(getByRole('heading', { name: /Pokédex/i })).toBeInTheDocument();
    }, { timeout: 5000 });

    // Find language selector
    const languageSelect = getByRole('combobox');

    // Change to Spanish
    fireEvent.change(languageSelect, { target: { value: 'es' } });

    await wait(() => {
      expect(getByText(/¡Hazte con todos!/i)).toBeInTheDocument();
    });

    // Change to Japanese
    fireEvent.change(languageSelect, { target: { value: 'ja' } });

    await wait(() => {
      expect(getByText(/ゲットだぜ！/i)).toBeInTheDocument();
    });
  });

  // Skip this test as it requires special setup for API error simulation
  test.skip('handles API errors gracefully', async () => {
    // This test would verify error handling
    // In real scenarios, use MSW or similar for API mocking
  });

  // Skip this test as it requires special setup for loading state
  test.skip('displays loading state initially', async () => {
    // This test would verify loading UI
    // In real scenarios, control async operations with MSW
  });
});
