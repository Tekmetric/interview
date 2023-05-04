import {
  Grid,
  TableContainer,
  Table,
  Paper,
  TableRow,
  TableCell,
  TableBody,
  Typography,
  Box,
  Button
} from '@mui/material';
import React from 'react';
import { CARS } from '../../constants';
import { useParams } from 'react-router-dom';
import PropTypes from 'prop-types';
//import logo from './logo.svg';

function getCarById(carId) {
  return CARS.find((car) => car.id == carId);
}

function CarMainDetails({ car }) {
  const mainDetails = [
    { title: 'Engine', key: 'engineCapacity' },
    { title: 'Manufacturing year', key: 'year' },
    { title: 'Color', key: 'color' }
  ];

  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableBody>
          {mainDetails.map(({ title, key }) => (
            <TableRow key={key} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
              <TableCell component="th" scope="row">
                <Typography sx={{ fontWeight: 'bold' }}>{title}</Typography>
              </TableCell>
              <TableCell align="right">{car[key]}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

const formatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD'

  // These options are needed to round to whole numbers if that's what you want.
  //minimumFractionDigits: 0, // (this suffices for whole numbers, but will print 2500.10 as $2,500.1)
  //maximumFractionDigits: 0, // (causes 2500.99 to be printed as $2,501)
});

function CarDetails() {
  const { id: carId } = useParams();
  const car = getCarById(carId);

  return (
    <Paper sx={{ margin: '5rem 10rem' }}>
      <Grid container>
        <Grid item xs={12} md={6} style={{ paddingTop: 0 }}>
          <img src={car.url} alt="Car Image" style={{ maxWidth: '100%' }} />
        </Grid>
        <Grid item xs={12} md={6} sx={{ px: 6, py: 2 }}>
          <Box display="flex" justifyContent="space-between" alignItems="center" sx={{ mb: 3 }}>
            <Typography variant="h3">
              {car.brand} - {car.model}
            </Typography>
            <Button>Edit</Button>
          </Box>
          <Typography variant="h5" color="primary" sx={{ fontWeight: 'bold', mb: 2 }}>
            {formatter.format(car.price)}
          </Typography>
          <Typography variant="p" color="text.secondary">
            {car.description}
          </Typography>
        </Grid>
      </Grid>
      <Grid container sx={{ mt: 2 }}>
        <CarMainDetails car={car} />
      </Grid>
    </Paper>
  );
}

CarMainDetails.propTypes = {
  car: PropTypes.object.isRequired
};

export default CarDetails;
