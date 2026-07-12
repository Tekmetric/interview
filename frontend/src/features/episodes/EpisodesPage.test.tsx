import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { EpisodesPage } from './EpisodesPage';

describe('EpisodesPage', () => {
  it('renders every season as an expanded disclosure with its episodes', async () => {
    renderWithProviders(<EpisodesPage />, { route: '/episodes' });

    const seasonOne = await screen.findByRole('button', { name: /Season 1/ });
    expect(seasonOne).toHaveAccessibleName('Season 1 (2 episodes)');
    expect(seasonOne).toHaveAttribute('aria-expanded', 'true');
    expect(screen.getByRole('link', { name: 'Pilot' })).toBeVisible();
    expect(screen.getByText('3 episodes found')).toBeVisible();
  });

  it('collapses and restores a season on toggle', async () => {
    const user = userEvent.setup();
    renderWithProviders(<EpisodesPage />, { route: '/episodes' });

    const seasonOne = await screen.findByRole('button', { name: /Season 1/ });
    await user.click(seasonOne);

    expect(seasonOne).toHaveAttribute('aria-expanded', 'false');
    expect(screen.queryByRole('link', { name: 'Pilot' })).not.toBeInTheDocument();
    // Season 2 is unaffected.
    expect(screen.getByRole('link', { name: 'A Rickle in Time' })).toBeVisible();

    await user.click(seasonOne);
    expect(screen.getByRole('link', { name: 'Pilot' })).toBeVisible();
  });

  it('filters episodes by name and keeps matching seasons open', async () => {
    const user = userEvent.setup();
    renderWithProviders(<EpisodesPage />, { route: '/episodes' });
    await screen.findByRole('button', { name: /Season 1/ });

    await user.type(screen.getByRole('searchbox', { name: 'Search episodes by name' }), 'rickle');

    expect(screen.getByText('1 episode found')).toBeVisible();
    expect(screen.getByRole('link', { name: 'A Rickle in Time' })).toBeVisible();
    expect(screen.queryByRole('button', { name: /Season 1/ })).not.toBeInTheDocument();
  });
});
