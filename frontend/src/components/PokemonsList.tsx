import React, { Fragment, ReactElement } from 'react'
import { useQuery } from '@tanstack/react-query'
import { fetchPokemons, Pokemon } from '../api/fetchPokemons'

const PokemonsList = (): ReactElement => {
  const { data } = useQuery(['pokemons'], fetchPokemons)
  const pokemons = data?.results

  return (
    <Fragment>
      {pokemons ? (
        pokemons.map((pokemon: Pokemon) => <p key={pokemon.name}>{pokemon.name}</p>)
      ) : (
        <p>Loading...</p>
      )}
    </Fragment>
  )
}

export default PokemonsList
