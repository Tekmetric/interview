import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import DarkModeToggle from './DarkModeToggle';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import themeReducer from '../store/themeSlice';
import '../i18n';

describe('DarkModeToggle', () => {
  const renderWithTheme = (component) => {
    const store = configureStore({
      reducer: {
        theme: themeReducer,
      },
    });

    return render(
      <Provider store={store}>
        {component}
      </Provider>
    );
  };

  it('renders dark mode toggle button', () => {
    const { getByRole } = renderWithTheme(<DarkModeToggle />);
    const button = getByRole('button');
    expect(button).toBeInTheDocument();
  });

  it('toggles between light and dark modes', () => {
    const { getByRole } = renderWithTheme(<DarkModeToggle />);
    const button = getByRole('button');

    // Initial state should be light mode (moon icon)
    expect(button.textContent).toContain('🌙');

    // Click to switch to dark mode
    fireEvent.click(button);

    // Should now show sun icon for light mode
    expect(button.textContent).toContain('☀️');

    // Click again to switch back
    fireEvent.click(button);

    // Should be back to moon icon
    expect(button.textContent).toContain('🌙');
  });

  it('updates icon when toggled', () => {
    const { getByRole } = renderWithTheme(<DarkModeToggle />);
    const button = getByRole('button');

    // Initial state (based on system preference)
    const initialIcon = button.textContent;

    // Toggle dark mode
    fireEvent.click(button);

    // Icon should change
    expect(button.textContent).not.toBe(initialIcon);

    // Toggle back
    fireEvent.click(button);

    // Should be back to original
    expect(button.textContent).toBe(initialIcon);
  });
});
