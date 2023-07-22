import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';
import { PokemonsContext, PokemonsContextType } from './contexts/PokemonsContext';
import HomePage from './pages/HomePage';

test('Test react application without provided mocks', () => {
  render(<App />);
  const linkElement = screen.getByText(/Loading pokemons.../i);
  expect(linkElement).toBeInTheDocument();
});

test('Test react application with pokemons context provided with mocked value', () => {
  const contextValue: PokemonsContextType = {
    pokemons: [
      {
        name: 'bulbasaur',
      },
      {
        name: 'pikachu',
      },
    ],
    fetchNextPage: () => {},
    isLoading: false,
    hasNextPage: true,
  };
  render(
    <PokemonsContext.Provider value={contextValue}>
      <HomePage />
    </PokemonsContext.Provider>,
  );
  const itemElementBulbasaur = screen.getByTestId('pokemonItem-bulbasaur');
  expect(itemElementBulbasaur).toBeInTheDocument();

  const itemElementPikachu = screen.getByTestId('pokemonItem-pikachu');
  expect(itemElementPikachu).toBeInTheDocument();
});
