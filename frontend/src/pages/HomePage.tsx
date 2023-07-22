import React, { ReactElement } from 'react';
import { Container } from '@mui/material';
import PokemonsList from '../components/pokemon/PokemonsList';

const HomePage = (): ReactElement => {
  return (
    <Container className='bg-blue-100 shadow-blue-400 shadow-xl p-10 m-10 min-h-screen h-full '>
      <PokemonsList />
    </Container>
  );
};

export default HomePage;
