import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import App from './App';

describe('App', () => {
  it('renders the shell and redirects the home route to characters', async () => {
    render(<App />);

    expect(screen.getByRole('navigation', { name: 'Primary' })).toBeVisible();
    // The index route redirects to /characters.
    expect(await screen.findByRole('heading', { level: 1, name: 'Characters' })).toBeVisible();
  });
});
