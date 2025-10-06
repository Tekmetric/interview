import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import DarkModeToggle from './DarkModeToggle';
import { ThemeProvider } from '../contexts/ThemeContext';
import '../i18n';

describe('DarkModeToggle', () => {
  const renderWithTheme = (component) => {
    return render(
      <ThemeProvider>
        {component}
      </ThemeProvider>
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

  it('updates document class when toggled', () => {
    const { getByRole } = renderWithTheme(<DarkModeToggle />);
    const button = getByRole('button');

    // Toggle to dark mode
    fireEvent.click(button);
    expect(document.documentElement.classList.contains('dark')).toBe(true);

    // Toggle back to light mode
    fireEvent.click(button);
    expect(document.documentElement.classList.contains('dark')).toBe(false);
  });
});
