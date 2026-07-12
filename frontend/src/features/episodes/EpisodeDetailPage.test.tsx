import { screen } from '@testing-library/react';
import { Route, Routes } from 'react-router';
import { describe, expect, it } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { EpisodeDetailPage } from './EpisodeDetailPage';

describe('EpisodeDetailPage', () => {
  it('shows the episode facts and its characters', async () => {
    renderWithProviders(
      <Routes>
        <Route path="/episodes/:episodeId" element={<EpisodeDetailPage />} />
      </Routes>,
      { route: '/episodes/1' },
    );

    expect(await screen.findByRole('heading', { level: 1, name: 'Pilot' })).toBeVisible();
    expect(screen.getByText('S01E01')).toBeVisible();
    // The English prose air date is parsed and re-rendered localized.
    expect(screen.getByText(/December 2, 2013/)).toBeVisible();
    expect(await screen.findByRole('link', { name: 'Rick Sanchez' })).toBeVisible();
    expect(screen.getByText('Characters (1)')).toBeVisible();
  });
});
