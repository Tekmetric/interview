import React, { Fragment, FunctionComponent, ReactElement, useEffect, useState } from 'react';
import { PaginatedPokemonsResponse, Pokemon } from '../../api/fetchPokemons';
import { Button, TextField } from '@mui/material';
import LoadingComponent from '../LoadingComponent';
import { useInfinitePokemons } from '../../hooks/useInfinitePokemons';
import PokemonListItem from './PokemonListItem';

const PokemonsList: FunctionComponent = (): ReactElement => {
  const [searchText, setSearchText] = useState<string>('');
  const [filteredPokemons, setFilteredPokemons] = useState<Pokemon[]>([]);

  const { data, fetchNextPage } = useInfinitePokemons();
  const pokemons =
    data?.pages.flatMap((results: PaginatedPokemonsResponse) => results.results) ?? [];
  useEffect(() => {
    const filteringResults = pokemons.filter((pokemon: Pokemon) =>
      pokemon.name.includes(searchText),
    );
    setFilteredPokemons(filteringResults);
  }, [data, searchText]);

  return (
    <Fragment>
      <div className='w-full flex flex-row mb-10 justify-center'>
        <TextField
          className='w-full md:w-2/4 lg:w-1/3'
          onChange={(e) => setSearchText(e.target.value)}
          id='search-input'
          variant='filled'
          label="Search by pokemon's name"
        />
      </div>
      {filteredPokemons ? (
        <div className='grid gap-5 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3'>
          {filteredPokemons.map((pokemon: Pokemon) => (
            <PokemonListItem key={pokemon.name} name={pokemon.name} />
          ))}
        </div>
      ) : (
        <LoadingComponent loadingText='Loading pokemons...' />
      )}
      <div className='w-full flex flex-row justify-center mt-10'>
        <Button onClick={() => fetchNextPage()} variant='contained'>
          More
        </Button>
      </div>
    </Fragment>
  );
};

export default PokemonsList;
