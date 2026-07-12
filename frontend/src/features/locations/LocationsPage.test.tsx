import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { LocationsPage } from './LocationsPage';

describe('LocationsPage', () => {
  it('lists locations with type and dimension', async () => {
    renderWithProviders(<LocationsPage />, { route: '/locations' });

    expect(await screen.findByRole('link', { name: 'Earth (C-137)' })).toBeVisible();
    expect(screen.getByText('Space station')).toBeVisible();
    expect(screen.getByText('2 locations found')).toBeVisible();
  });

  it('shows the empty state when the search matches nothing', async () => {
    const user = userEvent.setup();
    renderWithProviders(<LocationsPage />, { route: '/locations' });
    await screen.findByRole('link', { name: 'Earth (C-137)' });

    await user.type(
      screen.getByRole('searchbox', { name: 'Search locations by name' }),
      'blips and chitz',
    );

    expect(await screen.findByText('No locations found in this dimension')).toBeVisible();
  });
});
