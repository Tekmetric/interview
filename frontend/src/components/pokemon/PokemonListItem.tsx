import React, { ReactElement, useContext } from 'react';
import { Card, CardContent, CardMedia, Typography } from '@mui/material';
import { PokemonsContext, PokemonsContextType } from '../../contexts/PokemonsContext';
import Pokemon from '../../models/Pokemon';

type Props = {
  name: string;
};

const PokemonListItem = ({ name }: Props): ReactElement => {
  const { pokemons }: PokemonsContextType = useContext(PokemonsContext);
  const currentPokemonIndex = pokemons.findIndex((pokemon: Pokemon) => pokemon.name === name);

  return (
    <Card>
      <CardMedia
        component='div'
        sx={{ pt: '50%' }}
        image={`https://source.unsplash.com/random?${name},pokemon`}
      />
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography gutterBottom variant='h5' component='h3'>
          {name}
        </Typography>
        <Typography><b className="text-2xl">#{currentPokemonIndex} </b></Typography>
      </CardContent>
    </Card>
  );
};

export default PokemonListItem;
