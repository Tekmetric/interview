import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Route, Routes, useLocation } from 'react-router';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { renderWithProviders } from '../../test/renderWithProviders';
import { RandomCharacterButton } from './RandomCharacterButton';

function LocationProbe() {
  const location = useLocation();
  return <output data-testid="location">{location.pathname}</output>;
}

describe('RandomCharacterButton', () => {
  afterEach(() => vi.restoreAllMocks());

  it('navigates to a random character within the reported count', async () => {
    // 0.5 of the 25 fixture characters => id 13.
    vi.spyOn(Math, 'random').mockReturnValue(0.5);
    const user = userEvent.setup();

    renderWithProviders(
      <>
        <RandomCharacterButton />
        <Routes>
          <Route path="*" element={<LocationProbe />} />
        </Routes>
      </>,
    );

    const button = screen.getByRole('button', { name: 'Random character' });
    // Enabled once the character count has loaded.
    await waitFor(() => expect(button).toBeEnabled());
    await user.click(button);

    expect(screen.getByTestId('location')).toHaveTextContent('/characters/13');
  });
});
