import React, { Fragment, FunctionComponent, ReactElement } from 'react'
import { useQuery } from '@tanstack/react-query'
import { fetchPokemons, Pokemon } from '../../api/fetchPokemons'
import { Card, CardContent, CardMedia, Typography } from '@mui/material'
import LoadingComponent from '../LoadingComponent'

const PokemonsList: FunctionComponent = (): ReactElement => {
  const { data } = useQuery(['pokemons'], fetchPokemons)
  const pokemons = data?.results

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
    </Fragment>
  )
}

export default PokemonsList
