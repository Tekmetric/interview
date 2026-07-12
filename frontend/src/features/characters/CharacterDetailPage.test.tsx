import { screen } from '@testing-library/react';
import { Route, Routes } from 'react-router';
import { describe, expect, it } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { CharacterDetailPage } from './CharacterDetailPage';

function renderDetail(route: string) {
  return renderWithProviders(
    <Routes>
      <Route path="/characters/:characterId" element={<CharacterDetailPage />} />
    </Routes>,
    { route },
  );
}

describe('CharacterDetailPage', () => {
  it('shows the character facts with cross-links', async () => {
    renderDetail('/characters/1');

    expect(await screen.findByRole('heading', { level: 1, name: 'Rick Sanchez' })).toBeVisible();
    expect(screen.getByText('Alive')).toBeVisible();
    expect(screen.getByText('Human')).toBeVisible();
    // Origin links to its location page.
    expect(screen.getByRole('link', { name: 'Earth (C-137)' })).toHaveAttribute(
      'href',
      '/locations/1',
    );
    // Episode appearances are batch-fetched and linked.
    expect(await screen.findByRole('link', { name: 'Pilot' })).toHaveAttribute(
      'href',
      '/episodes/1',
    );
    expect(screen.getByText('Episodes (2)')).toBeVisible();
  });

  it('shows a friendly not-found state for a character id that does not exist', async () => {
    renderDetail('/characters/999');

    expect(
      await screen.findByText('This character does not exist in this dimension.'),
    ).toBeVisible();
  });
});
