import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { FavoritesPage } from './FavoritesPage';
import { FAVORITES_STORAGE_KEY } from './favoritesSlice';

describe('FavoritesPage', () => {
  it('shows an empty state before anything is favorited', () => {
    renderWithProviders(<FavoritesPage />, { route: '/favorites' });

    expect(screen.getByText('Nothing here yet')).toBeVisible();
  });

  it('renders each favorited entity type in its own section', async () => {
    renderWithProviders(<FavoritesPage />, {
      route: '/favorites',
      preloadedState: {
        favorites: { characters: [1], episodes: [1], locations: [3] },
      },
    });

    expect(await screen.findByRole('link', { name: 'Rick Sanchez' })).toBeVisible();
    expect(await screen.findByRole('link', { name: 'S01E01 — Pilot' })).toBeVisible();
    expect(await screen.findByRole('link', { name: 'Citadel of Ricks' })).toBeVisible();
  });

  it('unfavoriting from the card removes it and persists', async () => {
    const user = userEvent.setup();
    renderWithProviders(<FavoritesPage />, {
      route: '/favorites',
      preloadedState: {
        favorites: { characters: [1], episodes: [], locations: [] },
      },
    });
    await screen.findByRole('link', { name: 'Rick Sanchez' });

    await user.click(screen.getByRole('button', { name: 'Favorite' }));

    expect(screen.queryByRole('link', { name: 'Rick Sanchez' })).not.toBeInTheDocument();
    expect(JSON.parse(window.localStorage.getItem(FAVORITES_STORAGE_KEY) ?? '{}')).toEqual({
      characters: [],
      episodes: [],
      locations: [],
    });
  });
});
