import {
  Card,
  CardContent,
  Grid,
  CardMedia,
  Typography,
  CardActions,
  Button,
  Box,
  Pagination
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { CARS } from '../../shared/constants';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { truncate } from '../../shared/helpers';
import CarsFilters from './CarsFilters';
//import logo from './logo.svg';

function CarsList() {
  const [cars, setCars] = useState(CARS);
  const navigate = useNavigate();
  const [page, setPage] = useState(1);
  const handlePageChange = (event, val) => {
    setPage(val);
  };
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const newCars = CARS.filter(
      (car) =>
        car.brand.toLowerCase() === searchParams.get('brand') &&
        car.color.toLowerCase() === searchParams.get('color')
    );
    setCars(newCars);
  }, [searchParams]);

  return (
    <Box sx={{ position: 'relative', height: '100%' }}>
      <Box display="flex" flexDirection="column" alignItems="center" sx={{ mt: 5 }}>
        <CarsFilters />
        <Grid container spacing={2} sx={{ padding: '2rem 5rem' }}>
          {cars.map((car) => (
            <Grid item xs={12} md={4} lg={3} key={car.id}>
              <Card>
                <CardMedia sx={{ height: 140 }} image={car.url} title="green iguana" />
                <CardContent>
                  <Typography gutterBottom variant="h5" component="div">
                    {car.brand} - {car.model}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {truncate(car.description, 180)}
                  </Typography>
                </CardContent>
                <CardActions>
                  <Button size="small" onClick={() => navigate(`${car.id}`)}>
                    Details
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
        <Pagination count={10} page={page} onChange={handlePageChange} sx={{ mt: 5 }} />
      </Box>
    </Box>
  );
}

export default CarsList;
