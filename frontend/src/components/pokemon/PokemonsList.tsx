import React, { Fragment, FunctionComponent, ReactElement, useContext } from 'react';
import { Button } from '@mui/material';
import LoadingComponent from '../LoadingComponent';
import PokemonListItem from './PokemonListItem';
import SearchInput from '../SearchInput';
import usePokemonSearch, { UsePokemonSearchType } from '../../hooks/usePokemonSearch';
import { PokemonsContext, PokemonsContextType } from '../../contexts/PokemonsContext';
import Pokemon from '../../models/Pokemon';

const PokemonsList: FunctionComponent = (): ReactElement => {
  const { pokemons, fetchNextPage, hasNextPage } = useContext<PokemonsContextType>(PokemonsContext);
  const { filteredPokemons, setSearchText }: UsePokemonSearchType = usePokemonSearch(pokemons);

  return (
    <Fragment>
      <SearchInput setSearchText={setSearchText} label={"Search by pokemons's name"} />
      {filteredPokemons?.length > 0 ? (
        <div className='grid gap-5 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3'>
          {filteredPokemons.map((pokemon: Pokemon) => (
            <PokemonListItem key={pokemon.name} name={pokemon.name} />
          ))}
        </div>
      ) : (
        <LoadingComponent loadingText='Loading pokemons...' />
      )}
      <div className='w-full flex flex-row justify-center mt-10'>
        <Button onClick={() => fetchNextPage()} variant='contained' disabled={!hasNextPage}>
          More
        </Button>
      </div>
    </Fragment>
  );
};

export default PokemonsList;
