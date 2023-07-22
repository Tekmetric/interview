import { Pokemon } from '../api/fetchPokemons';
import { useState } from 'react';
import useDebouncedEffect from './useDebouncedEffect';

export type UsePokemonSearchType = {
  filteredPokemons: Pokemon[];
  setSearchText: (searchText: string) => void;
};

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
