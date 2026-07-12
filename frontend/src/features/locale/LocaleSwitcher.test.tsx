import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';

import { Layout } from '../../components/Layout';
import { renderWithProviders } from '../../test/renderWithProviders';
import { LOCALE_STORAGE_KEY } from './localeSlice';

describe('LocaleSwitcher', () => {
  it('switches every visible string to the selected language and persists it', async () => {
    const user = userEvent.setup();
    renderWithProviders(<Layout />);

    // English by default.
    expect(screen.getByRole('link', { name: 'Characters' })).toBeVisible();

    await user.selectOptions(screen.getByRole('combobox', { name: 'Language' }), 'ro-RO');

    expect(screen.getByRole('link', { name: 'Personaje' })).toBeVisible();
    expect(window.localStorage.getItem(LOCALE_STORAGE_KEY)).toBe('ro-RO');
    // The document language follows, so screen readers switch pronunciation.
    expect(document.documentElement.lang).toBe('ro-RO');
  });
});
