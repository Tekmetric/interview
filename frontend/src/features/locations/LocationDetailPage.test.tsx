import { screen } from '@testing-library/react';
import { Route, Routes } from 'react-router';
import { describe, expect, it } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { LocationDetailPage } from './LocationDetailPage';

describe('LocationDetailPage', () => {
  it('shows the location facts and its residents', async () => {
    renderWithProviders(
      <Routes>
        <Route path="/locations/:locationId" element={<LocationDetailPage />} />
      </Routes>,
      { route: '/locations/1' },
    );

    expect(await screen.findByRole('heading', { level: 1, name: 'Earth (C-137)' })).toBeVisible();
    expect(screen.getByText('Planet')).toBeVisible();
    expect(screen.getByText('Dimension C-1')).toBeVisible();
    expect(await screen.findByRole('link', { name: 'Rick Sanchez' })).toBeVisible();
    expect(screen.getByText('Residents (1)')).toBeVisible();
  });
});
