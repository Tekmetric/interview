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
  Fab,
  CircularProgress
} from '@mui/material';
import React, { useState } from 'react';

import { useNavigate, useSearchParams } from 'react-router-dom';
import { truncate } from '../../shared/helpers';
import CarsFilters from './CarsFilters';
import { useMutation, useQuery, useQueryClient } from 'react-query';
import { deleteCar, getCars } from '../api';
import AddIcon from '@mui/icons-material/Add';
import PropTypes from 'prop-types';
import ErrorComponent from '../../shared/components/ErrorComponent';
import ButtonWithProgress from '../../shared/components/ButtonWithProgress';
import DeletionConfirmationDialog from '../../shared/components/DeletionConfirmationDialog';
//import logo from './logo.svg';

function CarCardsList({ cars }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const mutation = useMutation(deleteCar, {
    onSuccess: () => {
      queryClient.resetQueries();
    }
  });
  const [isConfirmationDialogOpen, setIsConfirmationDialogOpen] = useState(false);
  const [carIdToDelete, setCarIdToDelete] = useState();

  const handleDelete = () => {
    mutation.mutate(carIdToDelete);
  };

  if (cars && cars.length <= 0) {
    return (
      <Typography variant="subtitle1">
        <em>No results found</em>
      </Typography>
    );
  }

  return (
    <>
      <Grid container spacing={2} sx={{ padding: '2rem 5rem' }}>
        {cars.map((car) => (
          <Grid item xs={12} md={4} lg={3} key={car._id}>
            <Card>
              <CardMedia sx={{ height: 200 }} image={car.url} title="green iguana" />
              <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                  {car.brand} - {car.model}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {truncate(car.description, 180)}
                </Typography>
              </CardContent>
              <CardActions sx={{ flexDirection: 'row-reverse' }}>
                <Button size="small" onClick={() => navigate(`${car._id}`)}>
                  Details
                </Button>
                <ButtonWithProgress
                  size="small"
                  color="error"
                  onClick={() => {
                    setCarIdToDelete(car._id);
                    setIsConfirmationDialogOpen(true);
                  }}
                  loading={mutation.isLoading}>
                  Delete
                </ButtonWithProgress>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
      <DeletionConfirmationDialog
        open={isConfirmationDialogOpen}
        onConfirm={handleDelete}
        onCancel={() => setIsConfirmationDialogOpen(false)}
      />
    </>
  );
}

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

  return (
    <Box sx={{ position: 'relative', height: '100%' }}>
      <Box display="flex" flexDirection="column" alignItems="center" sx={{ mt: 5 }}>
        <Grid container sx={{ pt: 0, pb: 2, px: 5 }}>
          <Grid item xs={12} md={8}>
            <CarsFilters />
          </Grid>
          <Grid item xs={12} md={4} display="flex" alignItems="center" justifyContent="flex-end">
            <Fab color="primary" onClick={() => navigate('/new')}>
              <AddIcon />
            </Fab>
          </Grid>
        </Grid>
        {isLoading && <CircularProgress />}
        {error && <ErrorComponent error={error} />}
        {!isLoading && !error && (
          <>
            <CarCardsList cars={data.cars} />
            <Pagination
              count={data.carPages}
              page={page}
              onChange={handlePageChange}
              sx={{ mt: 5 }}
            />
          </>
        )}
      </Box>
    </Box>
  );
}

CarCardsList.propTypes = {
  cars: PropTypes.array.isRequired
};

export default CarsList;
