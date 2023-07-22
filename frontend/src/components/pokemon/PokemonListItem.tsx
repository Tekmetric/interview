import React, { ReactElement } from 'react';
import { Card, CardContent, CardMedia, Typography } from '@mui/material';

type Props = {
  name: string;
};

const PokemonListItem = ({ name }: Props): ReactElement => {
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
      </CardContent>
    </Card>
  );
};

export default PokemonListItem;
