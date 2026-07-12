import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { http, HttpResponse } from 'msw';
import { describe, expect, it } from 'vitest';

import { server } from '../../test/msw/server';
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

  it('shows a retryable error for a section whose request failed instead of hiding it', async () => {
    const user = userEvent.setup();
    // The API rate-limits bursts with 429 — simulate it for the batch fetch.
    server.use(
      http.get('https://rickandmortyapi.com/api/location/:ids', () =>
        HttpResponse.json({ error: 'rate limited' }, { status: 429 }),
      ),
    );
    renderWithProviders(<FavoritesPage />, {
      route: '/favorites',
      preloadedState: {
        favorites: { characters: [], episodes: [], locations: [3] },
      },
    });

    expect(await screen.findByText('This section could not be loaded.')).toBeVisible();
    expect(screen.getByRole('heading', { level: 2, name: 'Locations' })).toBeVisible();

    // Once the throttle clears, retry recovers the section in place.
    server.resetHandlers();
    await user.click(screen.getByRole('button', { name: 'Try again' }));

    expect(await screen.findByRole('link', { name: 'Citadel of Ricks' })).toBeVisible();
    expect(screen.queryByText('This section could not be loaded.')).not.toBeInTheDocument();
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
