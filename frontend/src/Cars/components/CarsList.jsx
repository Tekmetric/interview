import {
  Card,
  CardContent,
  Grid,
  CardMedia,
  Typography,
  CardActions,
  Button,
  Box,
  Pagination,
  Fab
} from '@mui/material';
import React, { useState } from 'react';

import { useNavigate, useSearchParams } from 'react-router-dom';
import { truncate } from '../../shared/helpers';
import CarsFilters from './CarsFilters';
import { useQuery } from 'react-query';
import { getCars } from '../api';
import AddIcon from '@mui/icons-material/Add';
//import logo from './logo.svg';

function CarsList() {
  const navigate = useNavigate();

  const [page, setPage] = useState(1);
  const handlePageChange = (event, val) => {
    setPage(val);
  };

  const [searchParams] = useSearchParams({});
  const { data, isLoading, error } = useQuery(['/cars', searchParams.toString(), page], () =>
    getCars(searchParams, page)
  );
  console.log(data);

  if (isLoading) {
    return 'Loading...';
  }

  if (error) {
    return 'Error' + error.message;
  }

  const { cars, carPages } = data;

  return (
    <Box sx={{ position: 'relative', height: '100%' }}>
      <Box display="flex" flexDirection="column" alignItems="center" sx={{ mt: 5 }}>
        <Grid container sx={{ padding: '2rem 5rem' }}>
          <Grid item xs={12} md={8}>
            <CarsFilters />
          </Grid>
          <Grid item xs={12} md={4} display="flex" alignItems="center" justifyContent="flex-end">
            <Fab color="primary" onClick={() => navigate('/new')}>
              <AddIcon />
            </Fab>
          </Grid>
        </Grid>
        <Grid container spacing={2} sx={{ padding: '2rem 5rem' }}>
          {cars.map((car) => (
            <Grid item xs={12} md={4} lg={3} key={car._id}>
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
                  <Button size="small" onClick={() => navigate(`${car._id}`)}>
                    Details
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
        <Pagination count={carPages} page={page} onChange={handlePageChange} sx={{ mt: 5 }} />
      </Box>
    </Box>
  );
}

export default CarsList;
