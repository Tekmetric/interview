import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { ThemeToggle } from './ThemeToggle';
import { THEME_STORAGE_KEY } from './themeSlice';

describe('ThemeToggle', () => {
  it('toggles the theme and persists the choice', async () => {
    const user = userEvent.setup();
    renderWithProviders(<ThemeToggle />, { preloadedState: { theme: { mode: 'light' } } });

    const toggle = screen.getByRole('button', { name: 'Dark theme' });
    expect(toggle).toHaveAttribute('aria-pressed', 'false');

    await user.click(toggle);

    expect(toggle).toHaveAttribute('aria-pressed', 'true');
    // The persistence listener writes through to localStorage.
    expect(window.localStorage.getItem(THEME_STORAGE_KEY)).toBe('dark');
  });
});
