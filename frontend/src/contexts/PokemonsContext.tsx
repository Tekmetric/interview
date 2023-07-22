import React, { createContext, ReactElement } from 'react';
import { PaginatedPokemonsResponse } from '../api/fetchPokemons';
import { useInfinitePokemons } from '../hooks/useInfinitePokemons';
import Pokemon from '../models/Pokemon';

export type PokemonsContextType = {
  pokemons: Pokemon[];
  fetchNextPage: () => void;
  isLoading: boolean;
  hasNextPage: boolean | undefined;
};

const defaultContextValue: PokemonsContextType = {
  pokemons: [],
  fetchNextPage: () => {},
  isLoading: false,
  hasNextPage: false,
};

export const PokemonsContext = createContext<PokemonsContextType>(defaultContextValue);

export default function PokemonsContextProvider({
  children,
}: {
  children: ReactElement;
}): ReactElement {
  const { data, fetchNextPage, isLoading, hasNextPage } = useInfinitePokemons();
  const pokemons: Pokemon[] =
    data?.pages.flatMap((results: PaginatedPokemonsResponse) => results.results) ?? [];

  const value: PokemonsContextType = {
    pokemons,
    fetchNextPage,
    isLoading,
    hasNextPage,
  };

  return <PokemonsContext.Provider value={value}>{children}</PokemonsContext.Provider>;
}
