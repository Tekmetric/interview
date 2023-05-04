import { Card, CardContent, Grid, CardMedia, Typography, CardActions, Button } from '@mui/material';
import React from 'react';
import { CARS } from '../../constants';
import { useNavigate } from 'react-router-dom';
//import logo from './logo.svg';

function CarsList() {
  const cars = CARS;
  const navigate = useNavigate();

  return (
    <Grid container spacing={2} sx={{ padding: '2rem 5rem' }}>
      {cars.map((car) => (
        <Grid item xs={12} md={4} lg={3} key={car.id}>
          <Card>
            <CardMedia sx={{ height: 140 }} image={car.url} title="green iguana" />
            <CardContent>
              <Typography gutterBottom variant="h5" component="div">
                {car.name}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {car.description}
              </Typography>
            </CardContent>
            <CardActions>
              <Button
                size="small"
                onClick={() => {
                  console.log(car.id);
                  navigate(`${car.id}`);
                }}>
                Details
              </Button>
            </CardActions>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}

export default CarsList;
