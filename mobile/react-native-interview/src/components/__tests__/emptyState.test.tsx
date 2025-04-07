import React from 'react';
import { render } from '@testing-library/react-native';
import EmptyState from '../gallery/emptyState';
import { ThemeProvider } from '@/context/themeContext';
import { ThemeProvider as NavigationThemeProvider } from '@react-navigation/native';

import { mockTheme } from '@/test-utils/mocks/theme';

const renderWithTheme = (component: React.ReactElement) => {
  return render(
    <ThemeProvider>
      <NavigationThemeProvider value={mockTheme}>{component}</NavigationThemeProvider>
    </ThemeProvider>,
  );
};

describe('EmptyState', () => {
  it('renders correctly with default props', () => {
    const { getByText, getByTestId } = renderWithTheme(
      <EmptyState title="Test Title" description="Test Description" />,
    );

    expect(getByText('Test Title')).toBeTruthy();
    expect(getByText('Test Description')).toBeTruthy();

    const iconContainer = getByTestId('icon-container');
    expect(iconContainer).toBeTruthy();
  });

  it('renders correctly with custom icon', () => {
    const { getByTestId } = renderWithTheme(
      <EmptyState icon="camera" title="Test Title" description="Test Description" />,
    );

    const iconContainer = getByTestId('icon-container');
    expect(iconContainer).toBeTruthy();
  });
});
