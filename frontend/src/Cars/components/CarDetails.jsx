import { Box, Typography } from '@mui/material';
import React from 'react';
import { CARS } from '../../constants';
import { useParams } from 'react-router-dom';
//import logo from './logo.svg';

function getCarById(carId) {
  return CARS.find((car) => (car.id = carId));
}

function CarDetails() {
  const { id: carId } = useParams();
  const car = getCarById(carId);

  return (
    <Box display="flex" width="100%" alignItems="center" justifyContent="center">
      <Typography variant="h1"> {car.brand} </Typography>
    </Box>
  );
}

export default CarDetails;
