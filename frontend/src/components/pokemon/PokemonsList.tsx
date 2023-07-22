import React, { Fragment, FunctionComponent, ReactElement } from 'react';
import { useInfiniteQuery } from '@tanstack/react-query';
import { fetchPokemons, PaginatedPokemonsResponse, Pokemon } from '../../api/fetchPokemons';
import { Button, Card, CardContent, CardMedia, Typography } from '@mui/material';
import LoadingComponent from '../LoadingComponent';

export const DEFAULT_PAGE_SIZE: number = 5;
export const DEFAULT_OFFSET_SIZE: number = 5;

const PokemonsList: FunctionComponent = (): ReactElement => {
  const { data, fetchNextPage } = useInfiniteQuery({
    queryKey: ['pokemons'],
    queryFn: ({ pageParam = 0 }) =>
      fetchPokemons({
        offset: DEFAULT_OFFSET_SIZE * pageParam,
        limit: DEFAULT_PAGE_SIZE,
      }),
    getNextPageParam: (lastPage, allPages) =>
      lastPage.results.length === 0 ? undefined : allPages.length + 1,
  });
  const pokemons =
    data?.pages.flatMap((results: PaginatedPokemonsResponse) => results.results) ?? [];

  return (
    <Fragment>
      {pokemons ? (
        <div className='grid gap-5 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3'>
          {pokemons.map((pokemon: Pokemon) => (
            <Card key={pokemon.name}>
              <CardMedia />
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant='h5' component='h3'>
                  {pokemon.name}
                </Typography>
              </CardContent>
            </Card>
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
