import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Box, CircularProgress, Paper, Typography } from '@mui/material';
import { useFormik } from 'formik';
import carValidationSchema from '../validation';
import CarForm from './CarForm';
import { getCar, patchCar } from '../api';
import { useMutation, useQuery, useQueryClient } from 'react-query';
import ErrorComponent from '../../shared/components/ErrorComponent';

function CarEdit() {
  const { id: carId } = useParams();
  const { data: car, isLoading, error } = useQuery('/cars/' + carId, () => getCar(carId));
  const queryClient = useQueryClient();
  const mutation = useMutation(patchCar, {
    onSuccess: () => {
      queryClient.resetQueries();
      navigate(`/cars/${carId}`);
    }
  });
  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: { ...car },
    validationSchema: carValidationSchema,
    onSubmit: (values) => {
      mutation.mutate({ carId, values });
    }
  });

  return (
    <Box display="flex" flexDirection="column" alignItems="center" sx={{ pt: 5, px: 1 }}>
      <Paper sx={{ maxWidth: 'lg' }}>
        <Typography align="center" variant="h3" gutterBottom>
          Edit Car
        </Typography>
        {isLoading && <CircularProgress />}
        {error && <ErrorComponent error={error} />}
        {!isLoading && !error && <CarForm formik={formik} isLoading={mutation.isLoading} />}
      </Paper>
    </Box>
  );
}

export default CarEdit;
