/**
 * RTK Query API Slice
 *
 * Provides automatic caching, refetching, and state management for Pokemon API
 * Replaces manual async thunks with declarative API endpoints
 */

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { Pokemon } from '../types/pokemon';
import { logger } from '../lib/logger';

const POKEMON_API_BASE = 'https://pokeapi.co/api/v2';
const TOTAL_POKEMON = 1025; // Total number of Pokemon in PokeAPI (through Gen 9)

/**
 * RTK Query API definition
 */
export const pokemonApi = createApi({
  reducerPath: 'pokemonApi',
  baseQuery: fetchBaseQuery({ baseUrl: POKEMON_API_BASE }),

  // 24 hour cache
  keepUnusedDataFor: 86400, // seconds

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
          const batchSize = 100;
          const allPokemon: Pokemon[] = [];

          for (let i = 0; i < TOTAL_POKEMON; i += batchSize) {
            const batch = Array.from(
              { length: Math.min(batchSize, TOTAL_POKEMON - i) },
              (_, idx) => i + idx + 1
            );

            const batchResults = await Promise.all(
              batch.map(async (id) => {
                try {
                  const result = await fetchWithBQ(`/pokemon/${id}`);
                  if (result.error) {
                    logger.error(`Error fetching Pokemon ${id}:`, result.error);
                    return null;
                  }
                  return result.data as Pokemon;
                } catch (err) {
                  logger.error(`Exception fetching Pokemon ${id}:`, err);
                  return null;
                }
              })
            );

            allPokemon.push(...batchResults.filter((p): p is Pokemon => p !== null));

            logger.info(`Fetched ${allPokemon.length} / ${TOTAL_POKEMON} Pokemon`);
          }

          logger.info(`Successfully fetched ${allPokemon.length} Pokemon`);

          return { data: allPokemon };
        } catch (error: any) {
          logger.error('Error in getAllPokemon:', error);
          return {
            error: {
              status: 'CUSTOM_ERROR',
              error: error.message || 'Failed to fetch Pokemon data'
            }
          };
        }
      },
      providesTags: ['Pokemon'],
    }),

    /**
     * Fetch single Pokemon by ID
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
} = pokemonApi;

// Export endpoints for direct usage
export const { getAllPokemon, getPokemonById } = pokemonApi.endpoints;
