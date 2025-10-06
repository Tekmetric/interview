import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Pokemon } from '../types/pokemon';
import { getFromCache, setInCache } from '../lib/cache';
import { logger } from '../lib/logger';

const POKEDEX_MAX_ID = 1302;
const POKEAPI_BASE_URL = 'https://pokeapi.co/api/v2';
const CACHE_KEY_ALL_POKEMON = 'all_pokemon';

export interface PokemonState {
  data: Pokemon[];
  loading: boolean;
  error: string | null;
  searchTerm: string;
  isMetric: boolean;
}

const initialState: PokemonState = {
  data: [],
  loading: false,
  error: null,
  searchTerm: '',
  isMetric: true,
};

/**
 * Reduce Pokemon data to only essential fields for caching
 */
const reduceForCache = (pokemon: any): Pokemon => ({
  id: pokemon.id,
  name: pokemon.name,
  height: pokemon.height,
  weight: pokemon.weight,
  sprites: {
    front_default: pokemon.sprites?.front_default || null
  },
  types: pokemon.types?.map((t: any) => ({
    type: { name: t.type.name }
  })) || [],
  stats: pokemon.stats?.map((s: any) => ({
    stat: { name: s.stat.name },
    base_stat: s.base_stat
  })) || []
});

/**
 * Check API health
 */
const checkApiHealth = async () => {
  const response = await fetch(`${POKEAPI_BASE_URL}/pokemon/1`, {
    method: 'GET',
    headers: {
      'Accept': 'application/json'
    }
  });

  if (!response.ok) {
    throw new Error(`API returned status ${response.status}`);
  }

  const data = await response.json();

  if (!data.name || !data.id) {
    throw new Error('API response missing expected fields');
  }

  return true;
};

/**
 * Fetch all Pokemon data with caching
 */
export const fetchPokemonData = createAsyncThunk<Pokemon[], void, { rejectValue: string }>(
  'pokemon/fetchData',
  async (_, { rejectWithValue }) => {
    try {
      // Check cache first
      const cachedData = getFromCache<Pokemon[]>(CACHE_KEY_ALL_POKEMON);
      if (cachedData) {
        logger.info('Returning Pokemon data from cache');
        return cachedData;
      }

      // Check API health before fetching all data
      await checkApiHealth();

      logger.info('Fetching Pokemon data from API...');
      const pokemonFetches: Promise<any | null>[] = [];

      for (let i = 1; i <= POKEDEX_MAX_ID; i++) {
        pokemonFetches.push(
          fetch(`${POKEAPI_BASE_URL}/pokemon/${i}`)
            .then(response => response.ok ? response.json() : null)
            .catch(error => {
              logger.error(error);
              return null;
            })
        );
      }

      const values = await Promise.all(pokemonFetches);
      const validPokemon = values.filter((p): p is any => p !== null);

      // Reduce data size before caching
      const reducedPokemon = validPokemon.map(reduceForCache);

      // Cache the reduced results
      setInCache(CACHE_KEY_ALL_POKEMON, reducedPokemon);
      logger.info(`Cached ${reducedPokemon.length} Pokemon`);

      return reducedPokemon;
    } catch (error: any) {
      logger.error('Error fetching Pokemon data:', error);
      return rejectWithValue(error.message || 'Failed to fetch Pokemon data');
    }
  }
);

const pokemonSlice = createSlice({
  name: 'pokemon',
  initialState,
  reducers: {
    setSearchTerm: (state, action: PayloadAction<string>) => {
      state.searchTerm = action.payload;
    },
    toggleMetric: (state) => {
      state.isMetric = !state.isMetric;
    },
    setMetric: (state, action: PayloadAction<boolean>) => {
      state.isMetric = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchPokemonData.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchPokemonData.fulfilled, (state, action) => {
        state.loading = false;
        state.data = action.payload;
      })
      .addCase(fetchPokemonData.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to load Pokemon';
      });
  },
});

export const { setSearchTerm, toggleMetric, setMetric } = pokemonSlice.actions;
export default pokemonSlice.reducer;
