import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { CharactersPage } from './CharactersPage';

// jsdom has no layout, so the real window virtualizer cannot compute a
// visible range here. This pass-through renders every row; the actual
// windowing behavior is asserted in the Playwright e2e suite, where real
// geometry exists.
vi.mock('@tanstack/react-virtual', () => ({
  useWindowVirtualizer: (options: { count: number }) => ({
    getTotalSize: () => options.count * 360,
    getVirtualItems: () =>
      Array.from({ length: options.count }, (_, index) => ({
        index,
        key: index,
        start: index * 360,
      })),
    options: { scrollMargin: 0 },
    measureElement: () => {},
  }),
}));

describe('CharactersPage', () => {
  it('renders the first page of characters with the total count', async () => {
    renderWithProviders(<CharactersPage />, { route: '/characters' });

    expect(await screen.findByRole('link', { name: 'Rick Sanchez' })).toBeVisible();
    expect(screen.getByText('25 characters found')).toBeVisible();
    // Page one holds 20 of the 25 fixtures.
    expect(screen.getAllByRole('listitem')).toHaveLength(20);
  });

  it('appends the next page on Load more', async () => {
    const user = userEvent.setup();
    renderWithProviders(<CharactersPage />, { route: '/characters' });

    const loadMore = await screen.findByRole('button', { name: 'Load more characters' });
    await user.click(loadMore);

    await waitFor(() => expect(screen.getAllByRole('listitem')).toHaveLength(25));
    // All fixtures loaded — the API reports no further page, the button goes away.
    expect(screen.queryByRole('button', { name: 'Load more characters' })).not.toBeInTheDocument();
  });

  it('narrows the results while typing in the search field', async () => {
    const user = userEvent.setup();
    renderWithProviders(<CharactersPage />, { route: '/characters' });
    await screen.findByRole('link', { name: 'Rick Sanchez' });

    await user.type(screen.getByRole('searchbox', { name: 'Search characters by name' }), 'Morty');

    // One request after the debounce, not one per keystroke.
    expect(await screen.findByText('1 character found')).toBeVisible();
    expect(screen.getByRole('link', { name: 'Morty Smith' })).toBeVisible();
    expect(screen.queryByRole('link', { name: 'Rick Sanchez' })).not.toBeInTheDocument();
  });

  it("shows the empty state when the search matches nothing (the API's 404)", async () => {
    renderWithProviders(<CharactersPage />, { route: '/characters?name=nothing-matches-this' });

    expect(await screen.findByText('No beings found in this dimension')).toBeVisible();
    expect(screen.getByText('No characters found')).toBeVisible();
  });
});
