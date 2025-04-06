import React from 'react';
import { render } from '@testing-library/react-native';
import FilterHeaderButton from '../buttons/filterHeaderButton';
import { ThemeProvider } from '@react-navigation/native';
import { ArtCrimeFilters } from '../../types/artCrime';
import { mockTheme } from '../../test-utils/mocks/theme';

describe('FilterHeaderButton', () => {
  it('renders correctly with no filters', () => {
    const mockFilters: ArtCrimeFilters = {};

    const { toJSON } = render(
      <ThemeProvider value={mockTheme}>
        <FilterHeaderButton filters={mockFilters} onPress={() => {}} />
      </ThemeProvider>,
    );

    expect(toJSON()).toMatchSnapshot();
  });

  it('renders correctly with active filters', () => {
    const mockFilters: ArtCrimeFilters = {
      crimeCategory: 'theft',
      maker: 'Picasso',
      period: 'Modern',
    };

    const { toJSON } = render(
      <ThemeProvider value={mockTheme}>
        <FilterHeaderButton filters={mockFilters} onPress={() => {}} />
      </ThemeProvider>,
    );

    expect(toJSON()).toMatchSnapshot();
  });
});
