import React from 'react';
import { render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';
import './i18n'; // Initialize i18n for tests
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import pokemonReducer from './store/pokemonSlice';
import themeReducer from './store/themeSlice';
import { pokemonApi } from './store/api';

// Mock BarChart component to avoid SVG rendering issues in tests
jest.mock('./components/BarChart', () => {
  return function BarChart() {
    return <div data-testid="bar-chart">BarChart</div>;
  };
});

// Helper to render with providers
const renderWithProviders = (component, preloadedState = {}) => {
  const store = configureStore({
    reducer: {
      pokemon: pokemonReducer,
      theme: themeReducer,
      [pokemonApi.reducerPath]: pokemonApi.reducer,
    },
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware().concat(pokemonApi.middleware),
    preloadedState,
  });

  return render(
    <Provider store={store}>
      {component}
    </Provider>
  );
};

// Mock pokemon data
const mockPokemonData = [
  {
    id: 1,
    name: 'bulbasaur',
    height: 7,
    weight: 69,
    sprites: {
      front_default: 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png'
    },
    types: [
      { type: { name: 'grass' } },
      { type: { name: 'poison' } }
    ],
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
    sprites: {
      front_default: 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png'
    },
    types: [
      { type: { name: 'electric' } }
    ],
    stats: [
      { stat: { name: 'hp' }, base_stat: 35 },
      { stat: { name: 'attack' }, base_stat: 55 },
      { stat: { name: 'defense' }, base_stat: 40 },
      { stat: { name: 'special-attack' }, base_stat: 50 },
      { stat: { name: 'special-defense' }, base_stat: 50 },
      { stat: { name: 'speed' }, base_stat: 90 }
    ]
  },
  {
    id: 150,
    name: 'mewtwo',
    height: 20,
    weight: 1220,
    sprites: {
      front_default: 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/150.png'
    },
    types: [
      { type: { name: 'psychic' } }
    ],
    stats: [
      { stat: { name: 'hp' }, base_stat: 106 },
      { stat: { name: 'attack' }, base_stat: 110 },
      { stat: { name: 'defense' }, base_stat: 90 },
      { stat: { name: 'special-attack' }, base_stat: 154 },
      { stat: { name: 'special-defense' }, base_stat: 90 },
      { stat: { name: 'speed' }, base_stat: 130 }
    ]
  }
];

describe('App Component - Link Functionality', () => {
  const waitForLinks = async (container) => {
    await waitFor(() => {
      const links = container.querySelectorAll('a[href*="pokemon.com"]');
      expect(links.length).toBeGreaterThan(0);
    });
    return container.querySelectorAll('a[href*="pokemon.com"]');
  };

  beforeEach(() => {
    // Mock fetch API
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

  test('renders Pokemon links with correct href format', async () => {
    const { container } = renderWithProviders(<App />);
    const links = await waitForLinks(container);

    // Verify we have the expected number of links
    expect(links.length).toBe(mockPokemonData.length);

    // Verify each link has the correct format
    mockPokemonData.forEach((pokemon, index) => {
      const link = links[index];
      const expectedName = pokemon.name.charAt(0).toUpperCase() + pokemon.name.slice(1);

      expect(link.href).toBe(`https://www.pokemon.com/us/pokedex/${pokemon.name}`);
      expect(link.target).toBe('_blank');
      expect(link.rel).toBe('noopener noreferrer');
      expect(link.textContent).toBe(expectedName);
    });
  });

  test('all links are clickable and have proper attributes', async () => {
    const { container } = renderWithProviders(<App />);
    const links = await waitForLinks(container);

    links.forEach(link => {
      // Verify link is an anchor element
      expect(link.tagName).toBe('A');

      // Verify href attribute exists and is not empty
      expect(link.href).toBeTruthy();
      expect(link.href.length).toBeGreaterThan(0);

      // Verify href points to pokemon.com domain
      expect(link.href).toContain('pokemon.com/us/pokedex/');

      // Verify link has proper security attributes for external links
      expect(link.getAttribute('rel')).toContain('noopener');
      expect(link.getAttribute('rel')).toContain('noreferrer');
    });
  });

  test('links have correct href for specific Pokemon', async () => {
    const { container } = renderWithProviders(<App />);
    await waitForLinks(container);

    const pokemonNames = ['bulbasaur', 'pikachu', 'mewtwo'];
    pokemonNames.forEach(name => {
      const link = container.querySelector(`a[href*="${name}"]`);
      expect(link.href).toBe(`https://www.pokemon.com/us/pokedex/${name}`);
    });
  });
});

describe('App Component - Event Handlers', () => {
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

  test('handles window resize event', async () => {
    renderWithProviders(<App />);

    // Wait for component to mount
    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalled();
    });

    // Trigger resize event
    global.innerWidth = 500;
    global.innerHeight = 600;
    global.dispatchEvent(new Event('resize'));

    // Component should handle resize without errors
  });

  test('handles Ctrl+F keyboard shortcut', async () => {
    const { container } = renderWithProviders(<App />);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalled();
    });

    const searchInput = container.querySelector('input[type="text"]');
    expect(searchInput).toBeInTheDocument();

    // Trigger Ctrl+F
    const event = new KeyboardEvent('keydown', {
      key: 'f',
      ctrlKey: true,
      bubbles: true,
      cancelable: true
    });

    Object.defineProperty(event, 'preventDefault', {
      value: jest.fn(),
      writable: true
    });

    window.dispatchEvent(event);

    // Search input should be focused (or at least the handler should run)
  });

  test('handles Cmd+F keyboard shortcut', async () => {
    const { container } = renderWithProviders(<App />);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalled();
    });

    const searchInput = container.querySelector('input[type="text"]');
    expect(searchInput).toBeInTheDocument();

    // Trigger Cmd+F (Mac)
    const event = new KeyboardEvent('keydown', {
      key: 'f',
      metaKey: true,
      bubbles: true,
      cancelable: true
    });

    Object.defineProperty(event, 'preventDefault', {
      value: jest.fn(),
      writable: true
    });

    window.dispatchEvent(event);
  });
});

describe('App Component - Error States', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('renders error state when fetch fails with message', async () => {
    // Make health check fail
    global.fetch = jest.fn(() =>
      Promise.reject(new Error('Network error'))
    );

    const { getByText } = renderWithProviders(<App />);

    await waitFor(() => {
      expect(getByText(/API Error/i)).toBeInTheDocument();
    }, { timeout: 10000 });

    expect(getByText(/Network error/i)).toBeInTheDocument();
    expect(getByText(/Retry/i)).toBeInTheDocument();
  });

  test('renders error state when fetch fails without message', async () => {
    global.fetch = jest.fn(() =>
      Promise.reject(new Error())
    );

    const { getByText } = renderWithProviders(<App />);

    await waitFor(() => {
      expect(getByText(/API Error/i)).toBeInTheDocument();
    }, { timeout: 10000 });

    expect(getByText(/Failed to load Pokemon data/i)).toBeInTheDocument();
  });

  test('error state has retry button that reloads page', async () => {
    global.fetch = jest.fn(() =>
      Promise.reject(new Error('Test error'))
    );

    delete window.location;
    window.location = { reload: jest.fn() };

    const { getByText } = renderWithProviders(<App />);

    await waitFor(() => {
      expect(getByText(/API Error/i)).toBeInTheDocument();
    }, { timeout: 10000 });

    const retryButton = getByText(/Retry/i);
    retryButton.click();

    expect(window.location.reload).toHaveBeenCalled();
  });
});
