import { createSelector } from '@reduxjs/toolkit';
import type { RootState } from './store';
import type { Pokemon } from '../types/pokemon';

/**
 * Base selectors - direct access to state slices
 */
export const selectPokemonData = (state: RootState) => state.pokemon.data;
export const selectPokemonLoading = (state: RootState) => state.pokemon.loading;
export const selectPokemonError = (state: RootState) => state.pokemon.error;
export const selectSearchTerm = (state: RootState) => state.pokemon.searchTerm;
export const selectIsMetric = (state: RootState) => state.pokemon.isMetric;
export const selectIsDarkMode = (state: RootState) => state.theme.isDarkMode;

/**
 * Memoized selector: Filter Pokemon by search term
 * Only recalculates when data or searchTerm changes
 */
export const selectFilteredPokemon = createSelector(
  [selectPokemonData, selectSearchTerm],
  (data, searchTerm): Pokemon[] => {
    if (!searchTerm) return data;

    const query = searchTerm.toLowerCase();
    return data.filter(pokemon =>
      pokemon.name.toLowerCase().includes(query) ||
      pokemon.id.toString().includes(query) ||
      pokemon.types?.some(type => type.type.name.toLowerCase().includes(query))
    );
  }
);

/**
 * Memoized selector: Get Pokemon count
 */
export const selectPokemonCount = createSelector(
  [selectPokemonData],
  (data): number => data.length
);

/**
 * Memoized selector: Get filtered Pokemon count
 */
export const selectFilteredPokemonCount = createSelector(
  [selectFilteredPokemon],
  (filteredData): number => filteredData.length
);

/**
 * Memoized selector: Check if search is active
 */
export const selectIsSearching = createSelector(
  [selectSearchTerm],
  (searchTerm): boolean => searchTerm.length > 0
);

/**
 * Memoized selector: Get Pokemon by ID
 */
export const selectPokemonById = (id: number) => createSelector(
  [selectPokemonData],
  (data): Pokemon | undefined => data.find(pokemon => pokemon.id === id)
);

/**
 * Memoized selector: Get all Pokemon types (unique)
 */
export const selectAllPokemonTypes = createSelector(
  [selectPokemonData],
  (data): string[] => {
    const typesSet = new Set<string>();
    data.forEach(pokemon => {
      pokemon.types?.forEach(type => {
        typesSet.add(type.type.name);
      });
    });
    return Array.from(typesSet).sort();
  }
);

/**
 * Memoized selector: Get Pokemon stats
 */
export const selectPokemonStats = createSelector(
  [selectPokemonData],
  (data) => ({
    total: data.length,
    averageHeight: data.reduce((sum, p) => sum + (p.height || 0), 0) / data.length,
    averageWeight: data.reduce((sum, p) => sum + (p.weight || 0), 0) / data.length,
  })
);
