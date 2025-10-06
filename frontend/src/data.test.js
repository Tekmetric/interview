import { checkApiHealth, fetchPokemonData, invalidatePokemonCache } from './lib/data';
import { getFromCache, setInCache, clearAllCache } from './lib/cache';

describe('API Health Check', () => {
  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('returns healthy status when API responds correctly', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ id: 1, name: 'bulbasaur' })
      })
    );

    const result = await checkApiHealth();

    expect(result.status).toBe('healthy');
    expect(result.message).toBe('API is responding correctly');
    expect(result.timestamp).toBeDefined();
  });

  test('returns unhealthy status when API returns error status', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        status: 500
      })
    );

    const result = await checkApiHealth();

    expect(result.status).toBe('unhealthy');
    expect(result.message).toContain('500');
    expect(result.timestamp).toBeDefined();
  });

  test('returns unhealthy status when API response is missing fields', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ invalid: 'data' })
      })
    );

    const result = await checkApiHealth();

    expect(result.status).toBe('unhealthy');
    expect(result.message).toContain('missing expected fields');
    expect(result.timestamp).toBeDefined();
  });

  test('returns unhealthy status when network request fails', async () => {
    global.fetch = jest.fn(() =>
      Promise.reject(new Error('Network error'))
    );

    const result = await checkApiHealth();

    expect(result.status).toBe('unhealthy');
    expect(result.message).toContain('Network error');
    expect(result.timestamp).toBeDefined();
  });
});

describe('Pokemon Data Caching', () => {
  beforeEach(() => {
    // Clear cache and mocks before each test
    clearAllCache();
    jest.clearAllMocks();
    localStorage.clear();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('fetchPokemonData returns cached data when available', async () => {
    const mockPokemon = [
      { id: 1, name: 'bulbasaur' },
      { id: 2, name: 'ivysaur' }
    ];

    // Pre-populate cache
    setInCache('all_pokemon', mockPokemon);

    // Mock fetch should not be called
    global.fetch = jest.fn();

    const result = await fetchPokemonData();

    expect(result).toEqual(mockPokemon);
    expect(global.fetch).not.toHaveBeenCalled();
  });

  test('fetchPokemonData fetches from API and caches when cache is empty', async () => {
    const mockPokemon = { id: 1, name: 'bulbasaur', sprites: {} };

    // Mock health check
    global.fetch = jest.fn()
      .mockImplementationOnce(() =>
        Promise.resolve({
          ok: true,
          json: () => Promise.resolve({ id: 1, name: 'bulbasaur' })
        })
      )
      // Mock Pokemon fetches
      .mockImplementation(() =>
        Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockPokemon)
        })
      );

    const result = await fetchPokemonData();

    expect(result.length).toBeGreaterThan(0);
    expect(global.fetch).toHaveBeenCalled();

    // Verify data was cached
    const cachedData = getFromCache('all_pokemon');
    expect(cachedData).toBeDefined();
    expect(cachedData.length).toBe(result.length);
  });

  test('invalidatePokemonCache removes cached data', async () => {
    const mockPokemon = [{ id: 1, name: 'bulbasaur' }];

    // Pre-populate cache
    setInCache('all_pokemon', mockPokemon);

    // Verify cache has data
    expect(getFromCache('all_pokemon')).toEqual(mockPokemon);

    // Invalidate cache
    invalidatePokemonCache();

    // Verify cache is empty
    expect(getFromCache('all_pokemon')).toBeNull();
  });

  test('cache expires after TTL', () => {
    const mockPokemon = [{ id: 1, name: 'bulbasaur' }];

    // Set cache with very short TTL (1ms)
    setInCache('all_pokemon', mockPokemon, 1);

    // Wait for expiry
    return new Promise((resolve) => {
      setTimeout(() => {
        const cachedData = getFromCache('all_pokemon');
        expect(cachedData).toBeNull();
        resolve();
      }, 10);
    });
  });
});
