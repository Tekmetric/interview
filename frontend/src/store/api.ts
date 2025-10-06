/**
 * RTK Query API Slice
 *
 * Provides automatic caching, refetching, and state management for Pokemon API
 * Replaces manual async thunks with declarative API endpoints
 */

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { Pokemon, PokeApiPokemonResponse } from '../types/pokemon';
import { logger } from '../lib/logger';

const POKEMON_API_BASE = 'https://pokeapi.co/api/v2';
const TOTAL_POKEMON = 1025; // Total number of Pokemon in PokeAPI (through Gen 9)

// API Configuration
const CACHE_TTL_SECONDS = 86400; // 24 hours
const FETCH_BATCH_SIZE = 100; // Number of Pokemon to fetch in parallel

/**
 * Transform PokeAPI response to our Pokemon type
 */
const transformPokemonResponse = (apiResponse: PokeApiPokemonResponse): Pokemon => ({
  id: apiResponse.id,
  name: apiResponse.name,
  height: apiResponse.height || 0,
  weight: apiResponse.weight || 0,
  sprites: {
    front_default: apiResponse.sprites?.front_default || null
  },
  types: apiResponse.types?.map(t => ({
    type: { name: t.type.name as Pokemon['types'][0]['type']['name'] }
  })) || [],
  stats: apiResponse.stats?.map(s => ({
    stat: { name: s.stat.name },
    base_stat: s.base_stat
  })) || []
});

/**
 * Fetch a single Pokemon by ID
 */
const fetchSinglePokemon = async (
  id: number,
  fetchWithBQ: any
): Promise<Pokemon | null> => {
  try {
    const result = await fetchWithBQ(`/pokemon/${id}`);
    if (result.error) {
      logger.error(`Error fetching Pokemon ${id}:`, result.error);
      return null;
    }

    const apiResponse = result.data as PokeApiPokemonResponse;
    return transformPokemonResponse(apiResponse);
  } catch (err) {
    logger.error(`Exception fetching Pokemon ${id}:`, err);
    return null;
  }
};

/**
 * Fetch a batch of Pokemon in parallel
 */
const fetchPokemonBatch = async (
  batchIds: number[],
  fetchWithBQ: any
): Promise<Pokemon[]> => {
  const batchResults = await Promise.all(
    batchIds.map(id => fetchSinglePokemon(id, fetchWithBQ))
  );

  return batchResults.filter((p): p is Pokemon => p !== null);
};

/**
 * RTK Query API definition
 */
export const pokemonApi = createApi({
  reducerPath: 'pokemonApi',
  baseQuery: fetchBaseQuery({ baseUrl: POKEMON_API_BASE }),

  // 24 hour cache
  keepUnusedDataFor: CACHE_TTL_SECONDS,

  tagTypes: ['Pokemon'],

  endpoints: (builder) => ({
    /**
     * Fetch all Pokemon data
     * Uses automatic caching and deduplication
     */
    getAllPokemon: builder.query<Pokemon[], void>({
      async queryFn(_arg, _queryApi, _extraOptions, fetchWithBQ) {
        try {
          logger.info('Fetching all Pokemon data via RTK Query');

          // Health check
          const healthCheck = await fetchWithBQ('/pokemon/1');
          if (healthCheck.error) {
            return {
              error: {
                status: 'CUSTOM_ERROR',
                error: 'API health check failed'
              }
            };
          }

          // Fetch all Pokemon in parallel (limited batches)
          const allPokemon: Pokemon[] = [];

          for (let i = 0; i < TOTAL_POKEMON; i += FETCH_BATCH_SIZE) {
            const batchIds = Array.from(
              { length: Math.min(FETCH_BATCH_SIZE, TOTAL_POKEMON - i) },
              (_, idx) => i + idx + 1
            );

            const batchPokemon = await fetchPokemonBatch(batchIds, fetchWithBQ);
            allPokemon.push(...batchPokemon);

            logger.info(`Fetched ${allPokemon.length} / ${TOTAL_POKEMON} Pokemon`);
          }

          logger.info(`Successfully fetched ${allPokemon.length} Pokemon`);

          return { data: allPokemon };
        } catch (error) {
          const errorMessage = error instanceof Error ? error.message : 'Failed to fetch Pokemon data';
          logger.error('Error in getAllPokemon:', error);
          return {
            error: {
              status: 'CUSTOM_ERROR',
              error: errorMessage
            }
          };
        }
      },
      providesTags: ['Pokemon'],
    }),

    /**
     * Fetch single Pokemon by ID
     * Used for detail views and prefetching
     */
    getPokemonById: builder.query<Pokemon, number>({
      query: (id) => `/pokemon/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Pokemon', id }],
    }),
  }),
});

// Export hooks for usage in components
export const {
  useGetAllPokemonQuery,
  useGetPokemonByIdQuery,
  useLazyGetAllPokemonQuery,
  useLazyGetPokemonByIdQuery,
  // Prefetch hooks for optimistic data loading
  usePrefetch,
} = pokemonApi;

// Export endpoints for direct usage
export const { getAllPokemon, getPokemonById } = pokemonApi.endpoints;

/**
 * Prefetch utility for Pokemon data
 * Usage: const prefetchPokemon = usePrefetch('getPokemonById');
 *        <div onMouseEnter={() => prefetchPokemon(25)}>Pikachu</div>
 */
export const usePokemonPrefetch = () => {
  const prefetchPokemon = usePrefetch('getPokemonById');

  return {
    prefetchPokemon,
    prefetchAll: usePrefetch('getAllPokemon'),
  };
};
