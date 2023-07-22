import { Pokemon } from '../api/fetchPokemons';
import { useEffect, useState } from 'react';

export type UsePokemonSearchType = {
  filteredPokemons: Pokemon[];
  setSearchText: (searchText: string) => void;
};

const usePokemonSearch = (pokemons: Pokemon[]): UsePokemonSearchType => {
  const [searchText, setSearchText] = useState<string>('');
  const [filteredPokemons, setFilteredPokemons] = useState<Pokemon[]>([]);

  useEffect(() => {
    const filteringResult = pokemons.filter((pokemon) => pokemon.name.includes(searchText));
    setFilteredPokemons(filteringResult);
  }, [pokemons, searchText]);

  return { filteredPokemons, setSearchText };
};

export default usePokemonSearch;
