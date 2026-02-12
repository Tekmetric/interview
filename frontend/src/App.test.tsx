import React from 'react';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { ThemeProvider } from 'styled-components';

import App from './App';
import { store } from './store/store';
import { lightTheme } from './theme';

jest.mock('./components/NavigationBar/NavigationBar', () => () => (
  <div data-testid="navigation-bar">Mocked NavigationBar</div>
));

jest.mock('./routes/Routes', () => () => (
  <div data-testid="routes">Mocked Routes</div>
));

describe('App component', () => {
  test('renders NavigationBar and Routes inside themed container', () => {
    render(
      <Provider store={store}>
        <ThemeProvider theme={lightTheme}>
          <App />
        </ThemeProvider>
      </Provider>,
    );

    // Check that the mocked components render
    expect(screen.getByTestId('navigation-bar')).toBeInTheDocument();
    expect(screen.getByTestId('routes')).toBeInTheDocument();

    // Check background image styling is applied
    const container = screen.getByTestId('app-container');
    expect(container).toHaveStyle(
      "background-image: url('/images/anime_background.jpg')",
    );
  });
});
