import { useState } from 'react';
import useDebouncedEffect from './useDebouncedEffect';
import Pokemon from '../models/Pokemon';

export type UsePokemonSearchType = {
  filteredPokemons: Pokemon[];
  setSearchText: (searchText: string) => void;
};

/**
 * This hook does an in-place filtering over the already fetched items based on a provided searching text
 * Filters out pokemons whose name do not contain the searching text
 * @param pokemons
 */
const usePokemonSearch = (pokemons: Pokemon[]): UsePokemonSearchType => {
  const [searchText, setSearchText] = useState<string>('');
  const [filteredPokemons, setFilteredPokemons] = useState<Pokemon[]>([]);

  useDebouncedEffect(
    () => {
      const filteringResult = pokemons.filter((pokemon) => pokemon.name.includes(searchText));
      setFilteredPokemons(filteringResult);
    },
    [pokemons, searchText],
    500,
  );

  return { filteredPokemons, setSearchText };
};

export default usePokemonSearch;
