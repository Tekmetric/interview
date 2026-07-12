import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

import { readStorage } from '../../utils/storage';

export const FAVORITES_STORAGE_KEY = 'rm-wiki:favorites';

export type FavoriteEntityType = 'characters' | 'episodes' | 'locations';

export type FavoritesState = Record<FavoriteEntityType, number[]>;

const emptyState: FavoritesState = { characters: [], episodes: [], locations: [] };

function isIdArray(value: unknown): value is number[] {
  return Array.isArray(value) && value.every((item) => typeof item === 'number');
}

// Stored JSON is user-controlled data — validate the shape instead of
// trusting it, so a corrupted entry can never crash the app at boot.
export function getInitialFavorites(): FavoritesState {
  const stored = readStorage(FAVORITES_STORAGE_KEY);
  if (!stored) {
    return emptyState;
  }
  try {
    const parsed: unknown = JSON.parse(stored);
    if (
      typeof parsed === 'object' &&
      parsed !== null &&
      isIdArray((parsed as FavoritesState).characters) &&
      isIdArray((parsed as FavoritesState).episodes) &&
      isIdArray((parsed as FavoritesState).locations)
    ) {
      return parsed as FavoritesState;
    }
  } catch {
    // Fall through to the empty state.
  }
  return emptyState;
}

export interface FavoriteRef {
  entityType: FavoriteEntityType;
  id: number;
}

export const favoritesSlice = createSlice({
  name: 'favorites',
  initialState: getInitialFavorites,
  reducers: {
    favoriteToggled(state, action: PayloadAction<FavoriteRef>) {
      const { entityType, id } = action.payload;
      const ids = state[entityType];
      const index = ids.indexOf(id);
      if (index === -1) {
        ids.push(id);
      } else {
        ids.splice(index, 1);
      }
    },
  },
  selectors: {
    selectFavoriteIds: (state, entityType: FavoriteEntityType) => state[entityType],
    selectIsFavorite: (state, ref: FavoriteRef) => state[ref.entityType].includes(ref.id),
  },
});

export const { favoriteToggled } = favoritesSlice.actions;
export const { selectFavoriteIds, selectIsFavorite } = favoritesSlice.selectors;
