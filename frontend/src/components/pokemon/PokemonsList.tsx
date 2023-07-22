import React, { Fragment, FunctionComponent, ReactElement } from 'react';
import { PaginatedPokemonsResponse, Pokemon } from '../../api/fetchPokemons';
import { Button } from '@mui/material';
import LoadingComponent from '../LoadingComponent';
import { useInfinitePokemons } from '../../hooks/useInfinitePokemons';
import PokemonListItem from './PokemonListItem';
import SearchInput from '../SearchInput';
import usePokemonSearch from '../../hooks/usePokemonSearch';

const PokemonsList: FunctionComponent = (): ReactElement => {
  const { data, fetchNextPage } = useInfinitePokemons();

  const pokemons =
    data?.pages.flatMap((results: PaginatedPokemonsResponse) => results.results) ?? [];
  const { filteredPokemons, setSearchText } = usePokemonSearch(pokemons);

  return (
    <Fragment>
      <SearchInput setSearchText={setSearchText} label={"Search by pokemons's name"} />
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
