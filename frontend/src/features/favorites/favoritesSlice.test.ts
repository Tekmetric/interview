import { beforeEach, describe, expect, it } from 'vitest';

import {
  favoritesSlice,
  favoriteToggled,
  FAVORITES_STORAGE_KEY,
  getInitialFavorites,
  type FavoritesState,
} from './favoritesSlice';

const empty: FavoritesState = { characters: [], episodes: [], locations: [] };

describe('favoritesSlice', () => {
  it('adds an id on first toggle and removes it on the second', () => {
    const added = favoritesSlice.reducer(
      empty,
      favoriteToggled({ entityType: 'characters', id: 1 }),
    );
    expect(added.characters).toEqual([1]);

    const removed = favoritesSlice.reducer(
      added,
      favoriteToggled({ entityType: 'characters', id: 1 }),
    );
    expect(removed.characters).toEqual([]);
  });

  it('keeps entity types independent', () => {
    const state = favoritesSlice.reducer(empty, favoriteToggled({ entityType: 'episodes', id: 7 }));
    expect(state).toEqual({ characters: [], episodes: [7], locations: [] });
  });
});

describe('getInitialFavorites', () => {
  beforeEach(() => window.localStorage.clear());

  it('restores a valid stored state', () => {
    window.localStorage.setItem(
      FAVORITES_STORAGE_KEY,
      JSON.stringify({ characters: [1, 2], episodes: [], locations: [3] }),
    );
    expect(getInitialFavorites()).toEqual({ characters: [1, 2], episodes: [], locations: [3] });
  });

  it.each([
    ['corrupted JSON', '{not json'],
    ['wrong shape', JSON.stringify({ characters: 'nope' })],
  ])('falls back to empty for %s', (_label, stored) => {
    window.localStorage.setItem(FAVORITES_STORAGE_KEY, stored);
    expect(getInitialFavorites()).toEqual(empty);
  });
});
