import { createSelector } from '@reduxjs/toolkit';
import type { RootState } from './store';

/**
 * Pokemon Selectors
 *
 * Note: Pokemon data fetching is now handled by RTK Query (src/store/api.ts)
 * These selectors only manage UI state (search term, metric preference, theme)
 */

/**
 * Base selectors - direct access to state slices
 */
export const selectSearchTerm = (state: RootState) => state.pokemon.searchTerm;
export const selectIsMetric = (state: RootState) => state.pokemon.isMetric;
export const selectIsDarkMode = (state: RootState) => state.theme.isDarkMode;

/**
 * Memoized selector: Check if search is active
 */
export const selectIsSearching = createSelector(
  [selectSearchTerm],
  (searchTerm): boolean => searchTerm.length > 0
);
