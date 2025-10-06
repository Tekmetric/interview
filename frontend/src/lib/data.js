import { getFromCache, setInCache, removeFromCache, clearAllCache, clearExpiredCache, getCacheStats } from './cache';

const POKEDEX_MAX_ID = 1302;
const POKEAPI_BASE_URL = 'https://pokeapi.co/api/v2';
const CACHE_KEY_ALL_POKEMON = 'all_pokemon';

// Re-export cache utilities for external use
export { clearAllCache, clearExpiredCache, getCacheStats };

export const checkApiHealth = async () => {
  try {
    const response = await fetch(`${POKEAPI_BASE_URL}/pokemon/1`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      return {
        status: 'unhealthy',
        message: `API returned status ${response.status}`,
        timestamp: new Date().toISOString()
      };
    }

    const data = await response.json();

    // Verify the response has expected structure
    if (!data.name || !data.id) {
      return {
        status: 'unhealthy',
        message: 'API response missing expected fields',
        timestamp: new Date().toISOString()
      };
    }

    return {
      status: 'healthy',
      message: 'API is responding correctly',
      timestamp: new Date().toISOString()
    };
  } catch (error) {
    return {
      status: 'unhealthy',
      message: `API connection failed: ${error.message}`,
      timestamp: new Date().toISOString()
    };
  }
};

/**
 * Reduce Pokemon data to only essential fields for caching
 */
const reduceForCache = (pokemon) => ({
  id: pokemon.id,
  name: pokemon.name,
  height: pokemon.height,
  weight: pokemon.weight,
  sprites: {
    front_default: pokemon.sprites?.front_default
  },
  types: pokemon.types?.map(t => ({
    type: { name: t.type.name }
  })),
  stats: pokemon.stats?.map(s => ({
    stat: { name: s.stat.name },
    base_stat: s.base_stat
  }))
});

export const fetchPokemonData = async () => {
  try {
    // Check cache first
    const cachedData = getFromCache(CACHE_KEY_ALL_POKEMON);
    if (cachedData) {
      console.log('Returning Pokemon data from cache');
      return cachedData;
    }

    // Check API health before fetching all data
    const healthCheck = await checkApiHealth();
    if (healthCheck.status === 'unhealthy') {
      console.error('API health check failed:', healthCheck.message);
      throw new Error(healthCheck.message);
    }

    console.log('Fetching Pokemon data from API...');
    const pokemonFetches = [];
    for (let i = 1; i <= POKEDEX_MAX_ID; i++) {
      pokemonFetches.push(
        fetch(`${POKEAPI_BASE_URL}/pokemon/${i}`)
          .then(response => response.ok ? response.json() : null)
          .catch(error => {
            console.error(error);
            return null;
          })
      );
    }

    const values = await Promise.all(pokemonFetches);
    const validPokemon = values.filter(p => p !== null);

    // Reduce data size before caching
    const reducedPokemon = validPokemon.map(reduceForCache);

    // Cache the reduced results
    setInCache(CACHE_KEY_ALL_POKEMON, reducedPokemon);
    console.log(`Cached ${reducedPokemon.length} Pokemon`);

    return validPokemon;
  } catch (error) {
    console.error(error);
    return [];
  }
};

/**
 * Invalidate Pokemon data cache
 * This will force a fresh fetch on the next request
 */
export const invalidatePokemonCache = () => {
  removeFromCache(CACHE_KEY_ALL_POKEMON);
  console.log('Pokemon cache invalidated');
};
